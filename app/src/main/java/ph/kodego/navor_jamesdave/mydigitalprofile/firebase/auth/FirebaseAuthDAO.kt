package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.auth

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
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FirebaseAuthDAO {
    fun currentUser(): FirebaseUser?
    suspend fun registerUser(email: String, password: String): FirebaseUser?
    suspend fun signInUser(email: String, password: String): Boolean
    suspend fun updateUser(fields: Map<String, Any?>): Boolean
    suspend fun updateUserPassword(oldPassword: String, password: String): Boolean
    fun signOutUser()
}

class FirebaseAuthDAOImpl(private val context: Context): FirebaseAuthDAO {
    private val auth = FirebaseAuth.getInstance()

    override fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun registerUser(email: String, password: String): FirebaseUser? {
        val task: Task<AuthResult>
        try {
            task = auth.createUserWithEmailAndPassword(email, password)
            task.await()
        }catch (e: FirebaseAuthException){
            withContext(Main) {
                Toast.makeText(
                    context,
                    e.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
            return null
        }
        return if (task.isSuccessful) {
            task.result.user
        } else {
            null
        }
    }

    override suspend fun signInUser(email: String, password: String): Boolean {
        val task: Task<AuthResult>
        try {
            task = auth.signInWithEmailAndPassword(email, password)
            task.await()
        }catch (e: FirebaseAuthException){
            withContext(Main) {
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
            return false
        }
        return task.isSuccessful
    }

    override suspend fun updateUser(fields: Map<String, Any?>): Boolean {
        val user = currentUser()!!
        val request = userProfileChangeRequest {
            if (fields.containsKey(USER_DISPLAY_NAME)){
                displayName = fields[USER_DISPLAY_NAME].toString()
            }
            if (fields.containsKey(USER_PHOTO_URI)){
                photoUri = fields[USER_PHOTO_URI].toString().toUri()
            }
        }
        val task = user.updateProfile(request)
        task.await()
        return if (task.isSuccessful) true else{
            Log.e("UserProfile Update", task.exception?.message.toString())
            false
        }
    }

    override suspend fun updateUserPassword(oldPassword: String, password: String): Boolean {
        val user = currentUser()!!
        val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
        val task: Task<Void>
        try {
            user.reauthenticate(credential).await()
            task = user.updatePassword(password)
            task.await()
        }catch (e: FirebaseException){
            withContext(Main) {
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
            return false
        }
        return if (task.isSuccessful){
            signOutUser()
            withContext(Main) {
                Toast.makeText(
                    context,
                    "Password Updated! Please Sign in again",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        } else false
    }

    override fun signOutUser() {
        auth.signOut()
    }

    companion object{
        const val USER_DISPLAY_NAME = "displayName"
        const val USER_PHOTO_URI = "photoUri"
    }
}