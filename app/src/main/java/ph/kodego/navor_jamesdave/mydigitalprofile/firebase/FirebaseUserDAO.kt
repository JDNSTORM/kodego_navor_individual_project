package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface FirebaseUserDAO {
    /**
     * Creates a new User using Email and Password
     */
    suspend fun registerUser(email: String, password: String): FirebaseUser?
    suspend fun signInUser(email: String, password: String): Boolean
    suspend fun updateUser(fields: HashMap<String, Any?>): Boolean
    fun signOutUser()
    fun getCurrentUserID(): String
    suspend fun updateUserPassword(oldPassword: String, password: String): Boolean
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

    override suspend fun signInUser(email: String, password: String): Boolean {
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

    override suspend fun updateUser(fields: HashMap<String, Any?>): Boolean {
        val user = auth.currentUser!!
        val request = userProfileChangeRequest {
            if (fields.containsKey("firstName") && fields.containsKey("lastName")){
                displayName = "${fields["firstName"]} ${fields["lastName"]}"
            }
            if (fields.containsKey("photoUri")){
                photoUri = fields["photoUri"].toString().toUri()
            }
        }
        val task = user.updateProfile(request)
        task.await()
        return if (task.isSuccessful){
            true
        }else{
            Log.e("UserProfile Update", task.exception?.message.toString())
            false
        }
    }

    override fun signOutUser() {
        auth.signOut()
    }

    override fun getCurrentUserID(): String {
        val user = auth.currentUser
        return user?.uid ?: ""
    }

    override suspend fun updateUserPassword(oldPassword: String, password: String): Boolean {
        val user = auth.currentUser!!
        val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
        val task: Task<Void>
        try {
            user.reauthenticate(credential).await()
            task = user.updatePassword(password)
            task.await()
        }catch (e: FirebaseException){
            Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            return false
        }
        Toast.makeText(context, "Password Updated! Please Sign in again", Toast.LENGTH_SHORT).show()
        return task.isSuccessful
    }

}