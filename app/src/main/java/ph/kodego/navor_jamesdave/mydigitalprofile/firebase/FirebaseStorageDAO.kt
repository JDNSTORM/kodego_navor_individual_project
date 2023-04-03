package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

interface FirebaseStorageDAO {
    suspend fun uploadAvatar(uri: Uri)
}

class FirebaseStorageDAOImpl(context: Context): FirebaseUserDAOImpl(context), FirebaseStorageDAO{
    internal val firebaseStorage = FirebaseStorage.getInstance()
    private val parentTree = getCurrentUserID()
    private val imageTree = "images"

    override suspend fun uploadAvatar(uri: Uri) { //TODO
        val reference = firebaseStorage
            .getReference(parentTree)
            .child(imageTree)
            .child(uri.lastPathSegment.toString())
        val task: UploadTask = reference.putFile(uri)
        task.await()
        if (task.isSuccessful){
            Log.d("Avatar Upload", task.result.toString())
            val urlTask = reference.downloadUrl
            urlTask.await()
            if (urlTask.isSuccessful){
                Log.d("DownloadURL", urlTask.result.toString())
            }else{
                Log.e("DownloadURL", task.exception!!.message!!)
            }
        }else{
            Log.e("Avatar Upload", task.exception!!.message!!)
        }
    }
}