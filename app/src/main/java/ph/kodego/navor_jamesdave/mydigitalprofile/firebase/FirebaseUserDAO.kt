package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface FirebaseUserDAO {
    suspend fun registerUser(email: String, password: String): FirebaseUser?
    suspend fun signInUser(email: String, password: String): Boolean
    fun signOutUser()
    fun getCurrentUserID(): String
}

open class FirebaseUserDAOImpl(internal val context: Context): FirebaseUserDAO{
    internal val auth = FirebaseAuth.getInstance()
    internal val fireStore = FirebaseFirestore.getInstance()

    override suspend fun registerUser(email: String, password: String): FirebaseUser? {
        val task = auth.createUserWithEmailAndPassword(email, password)
        task.await()
        return if (task.isSuccessful){
            task.result.user
        }else{
            Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
            null
        }
    }

    override suspend fun signInUser(email: String, password: String): Boolean { //TODO: AsyncTask
        val task = auth.signInWithEmailAndPassword(email, password)
        task.await()
        return if (task.isSuccessful){
            true
        }else{
            Toast.makeText(context, task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            false
        }
    }

    override fun signOutUser() {
        auth.signOut()
    }

    override fun getCurrentUserID(): String {
        val currentUser = auth.currentUser
        return currentUser?.uid ?: ""
    }

}