package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await

interface FirebaseStorageDAO {
    suspend fun uploadAccountPhoto(uri: Uri): Uri?
    suspend fun updateAccountPhoto(uri: Uri): String
    suspend fun deleteAccountPhoto(url: String): Boolean
}

class FirebaseStorageDAOImpl(context: Context): FirebaseUserDAOImpl(context), FirebaseStorageDAO{
    internal val firebaseStorage = FirebaseStorage.getInstance()
    private val parentTree = getCurrentUserID()
    private val imageTree = "images"

    override suspend fun uploadAccountPhoto(uri: Uri): Uri? {
        val reference = firebaseStorage
            .getReference(parentTree)
            .child(imageTree)
            .child(uri.lastPathSegment.toString())
        val task: UploadTask = reference.putFile(uri)
        task.await()
        if (task.isSuccessful){
            Log.d("Avatar Upload", task.result.toString())
//            val urlTask = reference.downloadUrl
            val urlTask = task.snapshot.metadata!!.reference!!.downloadUrl
            urlTask.await()
            if (urlTask.isSuccessful){
                Log.d("DownloadURL", urlTask.result.toString())
                return urlTask.result
            }else{
                Log.e("DownloadURL", task.exception!!.message!!)
                return null
            }
        }else{
            Log.e("Avatar Upload", task.exception!!.message!!)
            return null
        }
    }

    override suspend fun updateAccountPhoto(uri: Uri): String {
        val storageUri = uploadAccountPhoto(uri)
        if (storageUri != null){
            val user = auth.currentUser!!
            val oldReference = firebaseStorage.getReferenceFromUrl(user.photoUrl.toString())
            val newReference = firebaseStorage.getReferenceFromUrl(storageUri.toString())
            if (oldReference != newReference) {
                deleteAccountPhoto(user.photoUrl.toString())
            }
            val photoUriUpdate = userProfileChangeRequest {
                photoUri = storageUri
            }
            val task = user.updateProfile(photoUriUpdate)
            task.await()
            if (!task.isSuccessful){
                Log.e("PhotoUpdate", task.exception?.message.toString())
            }
        }
        return storageUri.toString()
    }

    override suspend fun deleteAccountPhoto(url: String): Boolean {
        try {
            val imageReference = firebaseStorage.getReferenceFromUrl(url)
            val task = imageReference.delete()
            task.await()
            Log.d("Photo Deletion", task.exception?.message.toString())
            return task.isSuccessful
        }catch (e: StorageException){
            Log.e("Photo Deletion", e.message.toString())
            return false
        }
    }
}