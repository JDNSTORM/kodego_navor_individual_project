package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.*

class Firebase(private val firebaseInterface: FirebaseInterface? = null) {
    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    private fun registerAccount(account: Account){
        firebaseInterface as FirebaseRegisterInterface //TODO: Check if plausible

        fireStore
            .collection(Constants.CollectionAccounts)
            .document(getCurrentUserID())
            .set(account, SetOptions.merge())
            .addOnSuccessListener {
                FirebaseAuth.getInstance().signOut()
                firebaseInterface.hideProgressDialog()
                firebaseInterface.accountRegistrationSuccess()
            }
            .addOnFailureListener {
                Log.e("Account Registration", it.message.toString())

                firebaseInterface.hideProgressDialog()
                firebaseInterface.accountRegistrationFail()
            }
    }

    fun registerUser(firstName: String, lastName: String, email: String, password: String){
        firebaseInterface as FirebaseRegisterInterface
        firebaseInterface.showProgressDialog()

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val firebaseUser: FirebaseUser = task.result!!.user!!
                val registeredEmail = firebaseUser.email!!
                val account = Account(firebaseUser.uid, firstName, lastName, email)
                registerAccount(account)
            }else{
                firebaseInterface.hideProgressDialog()
                firebaseInterface.userRegistrationFail(task.exception!!.message!!)
            }
        }
    }

    fun signInUser(email: String, password: String){
        firebaseInterface as FirebaseLoginInterface
        firebaseInterface.showProgressDialog()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                firebaseInterface.signInSuccessful()
            }else{
                firebaseInterface.hideProgressDialog()
                firebaseInterface.signInFailed(task.exception!!.message!!)
            }
        }
    }

    fun signOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

    fun getAccount(){
        firebaseInterface as FirebaseAccountInterface
        firebaseInterface.showProgressDialog()
        fireStore
            .collection(Constants.CollectionAccounts)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i("Account Document Retrieved", document.toString())
                val account = document.toObject(Account::class.java)!!
                firebaseInterface.hideProgressDialog()
                firebaseInterface.getAccountSuccess(account)
            }
            .addOnFailureListener {  e ->
                Log.e("Account Error", e.message.toString())
                firebaseInterface.hideProgressDialog()
                firebaseInterface.getAccountFailed()
            }
    }

    fun getCurrentUserID(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }
}