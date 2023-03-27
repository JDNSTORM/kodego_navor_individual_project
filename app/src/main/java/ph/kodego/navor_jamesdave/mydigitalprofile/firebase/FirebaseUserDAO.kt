package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

interface FirebaseUserDAO {
    fun registerUser(email: String, password: String): FirebaseUser?
    fun signInUser(email: String, password: String): Boolean
    fun signOutUser()
    fun getCurrentUserID(): String
}

open class FirebaseUserDAOImpl(internal val context: Context): FirebaseUserDAO{
    internal val auth = FirebaseAuth.getInstance()
    internal val fireStore = FirebaseFirestore.getInstance()

    override fun registerUser(email: String, password: String): FirebaseUser? {
        val task = auth.createUserWithEmailAndPassword(email, password)
        return if (task.isSuccessful){
            task.result.user
        }else{
            Toast.makeText(context, task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
            null
        }
    }

    override fun signInUser(email: String, password: String): Boolean { //TODO: AsyncTask
        val task = auth.signInWithEmailAndPassword(email, password)
        Log.d("Sign In", task.result.toString())
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