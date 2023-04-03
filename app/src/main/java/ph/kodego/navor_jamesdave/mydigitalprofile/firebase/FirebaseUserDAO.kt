package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface FirebaseUserDAO {
    suspend fun registerUser(email: String, password: String): FirebaseUser?
    suspend fun signInUser(email: String, password: String): Boolean
    fun signOutUser()
    fun getCurrentUserID(): String
    suspend fun updateUserPassword(password: String): Boolean
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
        val task: Task<AuthResult>
        try {
            task = auth.signInWithEmailAndPassword(email, password)
            task.await()
        }catch (e: FirebaseAuthException){
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            return false
        }
        return task.isSuccessful
    }

    override fun signOutUser() {
        auth.signOut()
    }

    override fun getCurrentUserID(): String {
        val user = auth.currentUser
        return user?.uid ?: ""
    }

    override suspend fun updateUserPassword(password: String): Boolean {
        val user = auth.currentUser!!
        val task: Task<Void>
        try {
            task = user.updatePassword(password)
            task.await()
        }catch (e: FirebaseException){
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            return false
        }
        return task.isSuccessful
    }

}