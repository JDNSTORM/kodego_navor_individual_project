package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await

interface FirebaseStorageDAO {
    suspend fun uploadImage(uri: Uri): Uri?

    /**
     * Uploads Image into storage then checks if the uploaded image is the same as the previous Account Photo
     * If not, it will remove the old image.
     */
    suspend fun updateAccountPhoto(uri: Uri): Boolean
    suspend fun deleteImage(url: String): Boolean
}

class FirebaseStorageDAOImpl(context: Context): FirebaseAccountDAOImpl(context), FirebaseStorageDAO{
    internal val firebaseStorage = FirebaseStorage.getInstance()
    private val parentTree = getCurrentUserID()
    private val imageTree = "images"

    override suspend fun uploadImage(uri: Uri): Uri? {
//        val filename = "${System.currentTimeMillis()}.${MimeTypeMap.getSingleton().getMimeTypeFromExtension(context.contentResolver.getType(uri))}"
        val filename = getFileName(uri)
        Log.d("FileName", filename)
        val reference = firebaseStorage
            .getReference(parentTree)
            .child(imageTree)
            .child(filename)
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

    override suspend fun updateAccountPhoto(uri: Uri): Boolean {
        val storageUri = uploadImage(uri)
        if (storageUri != null){
            val user = auth.currentUser!!
            Log.d("User PhotoUrl", user.photoUrl.toString())
            if (user.photoUrl != null && user.photoUrl.toString().contains("firebasestorage")) {
                val oldReference = firebaseStorage.getReferenceFromUrl(user.photoUrl.toString())
                val newReference = firebaseStorage.getReferenceFromUrl(storageUri.toString())
                if (oldReference != newReference) {
                    deleteImage(user.photoUrl.toString())
                }
            }
            val userProfileChangeMap: HashMap<String, Any?> = hashMapOf("photoUri" to storageUri)
            return if (updateUser(userProfileChangeMap)){
                val updateImageMap = HashMap<String, Any?>()
                updateImageMap["image"] = storageUri.toString()
                updateAccount(updateImageMap)
            }else{
                false
            }
        }else{
            return false
        }
    }

    override suspend fun deleteImage(url: String): Boolean {
        try {
            val imageReference = firebaseStorage.getReferenceFromUrl(url)
            val task = imageReference.delete()
            task.await()
            return task.isSuccessful
        }catch (e: StorageException){
            Log.e("Photo Deletion", e.message.toString())
            return false
        }
    }

    private fun getFileName(uri: Uri): String{
        val fileExtension = if (uri.scheme == ContentResolver.SCHEME_CONTENT){
            val mimeType = context.contentResolver.getType(uri)
            Log.d("MimeType", mimeType.toString())
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
        }else{
            MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        }
        val fileName = StringBuilder()
        fileName.append(System.currentTimeMillis())
        fileName.append('.')
        fileName.append(fileExtension)

        return fileName.toString()
    }
}