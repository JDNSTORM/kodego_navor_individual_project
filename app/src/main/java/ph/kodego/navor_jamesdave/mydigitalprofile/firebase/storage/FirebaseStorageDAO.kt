package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.storage

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.coroutines.tasks.await

interface FirebaseStorageDAO {
    suspend fun uploadFile(childTree: String, uri: Uri): Uri
    suspend fun deleteFile(url: String)
}

class FirebaseStorageDAOImpl(private val context: Context): FirebaseStorageDAO{
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val parentTree: String by lazy { FirebaseAuth.getInstance().currentUser!!.uid }

    override suspend fun uploadFile(childTree: String, uri: Uri): Uri {
        val fileName = generateFileName(uri)
        val reference = storage
            .getReference(parentTree)
            .child(childTree)
            .child(fileName)
        reference.putFile(uri).await()
        return reference.downloadUrl.await()

//        task.await()
//        if (task.isSuccessful){
//            Log.d("Upload", task.result.toString())
//            val download = reference.downloadUrl
////            val urlTask = task.snapshot.metadata!!.reference!!.downloadUrl
//            download.await()
//            return if (download.isSuccessful){
//                Log.d("DownloadURL", download.result.toString())
//                download.result
//            }else{
//                Log.e("DownloadURL", task.exception!!.message!!)
//                null
//            }
//        }else{
//            Log.e("Upload", task.exception!!.message!!)
//            return null
//        }
    }

    override suspend fun deleteFile(url: String) {
        if (url.isNullOrEmpty()) return
        storage.getReferenceFromUrl(url).delete().await()

//        return try {
//            val imageReference = storage.getReferenceFromUrl(url)
//            val task = imageReference.delete()
//            task.await()
//            task.isSuccessful
//        }catch (e: StorageException){
//            Log.e("Delete", e.message.toString())
//            false
//        }
    }

    private fun generateFileName(uri: Uri): String{
        val fileName = StringBuilder()
        fileName.append(System.currentTimeMillis())
        fileName.append('.')
        fileName.append(getFileExtension(uri))

        return fileName.toString()
    }

    private fun getFileExtension(uri: Uri): String{
        return when(uri.scheme){
            ContentResolver.SCHEME_CONTENT -> {
                val mimeType = context.contentResolver.getType(uri)
                Log.d("MimeType", mimeType.toString())
                MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType).toString()
            }
            ContentResolver.SCHEME_FILE -> uri.toFile().extension
            else -> MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        }
    }

    companion object{
        const val IMAGE_TREE = "images"
    }
}