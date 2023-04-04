package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await

interface FirebaseStorageDAO {
    suspend fun uploadAccountPhoto(uri: Uri): Uri?
    suspend fun updateAccountPhoto(uri: Uri): String
}

class FirebaseStorageDAOImpl(context: Context): FirebaseUserDAOImpl(context), FirebaseStorageDAO{
    internal val firebaseStorage = FirebaseStorage.getInstance()
    private val parentTree = getCurrentUserID()
    private val imageTree = "images"

    override suspend fun uploadAccountPhoto(uri: Uri): Uri? { //TODO
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
        if (storageUri != null){ //TODO: Delete old photo
            val user = auth.currentUser!!
            val photoURIUpdate = userProfileChangeRequest {
                photoUri = storageUri
            }
            val task = user.updateProfile(photoURIUpdate)
            task.await()
            if (!task.isSuccessful){
                Log.e("PhotoUpdate", task.exception?.message.toString())
            }
        }
        return storageUri.toString()
    }
}