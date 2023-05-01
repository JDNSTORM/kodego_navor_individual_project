package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.EmailAddress
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.*

@Deprecated("Implemented DAO")
class FirebaseClient(private val firebaseInterface: FirebaseInterface? = null) {
    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    private fun registerContactInformation(contactInformation: ContactInformation){
        fireStore
            .collection(IntentBundles.CollectionContactInformation)
            .document(contactInformation.contactInformationID)
            .set(contactInformation, SetOptions.merge())
            .addOnSuccessListener {
                Log.i("Contact Registration", "Successful")
                if (contactInformation.emailAddress != null) {
                    registerEmail(contactInformation.emailAddress!!)
                }
            }
            .addOnFailureListener {  e ->
                Log.e("Contact Registration", e.message.toString())
            }
    }
    private fun registerEmail(emailAddress: EmailAddress){
        fireStore
            .collection(IntentBundles.CollectionContactInformation)
            .document(emailAddress.contactInformationID)
            .collection(IntentBundles.CollectionEmail)
            .document(emailAddress.contactInformationID)
            .set(emailAddress, SetOptions.merge())
            .addOnSuccessListener {
                Log.i("Email Registration", "Successful")
            }
            .addOnFailureListener {  e ->
                Log.e("Email Registration", e.message.toString())
            }
    }

    private fun registerAccount(account: Account){
        firebaseInterface as FirebaseRegisterInterface

        fireStore
            .collection(IntentBundles.CollectionAccounts)
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
                val contactInformationReference = fireStore.collection(IntentBundles.CollectionContactInformation).document()
                val emailDocument = fireStore.collection(IntentBundles.CollectionEmail).document()

                val emailAddress = EmailAddress(emailDocument.id, contactInformationReference.id, registeredEmail)
                val contactInformation = ContactInformation(contactInformationReference.id)
                contactInformation.emailAddress = emailAddress
                val account = Account(firebaseUser.uid, firstName, lastName, contactInformation.contactInformationID)
                account.contactInformation = contactInformation

                registerContactInformation(contactInformation)
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
        auth.signOut()
    }

    fun getAccount(){
        firebaseInterface as FirebaseAccountInterface
        firebaseInterface.showProgressDialog()
        fireStore
            .collection(IntentBundles.CollectionAccounts)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i("Account Document Retrieved", document.toString())
                val account = document.toObject(Account::class.java)!!
                getAccountContactInformation(account)
            }
            .addOnFailureListener {  e ->
                Log.e("Account Error", e.message.toString())
                firebaseInterface.hideProgressDialog()
                firebaseInterface.getAccountFailed()
            }
    }

    private fun getAccountContactInformation(account: Account){
        firebaseInterface as FirebaseAccountInterface
        fireStore
            .collection(IntentBundles.CollectionContactInformation)
            .document(account.contactInformationID)
            .get()
            .addOnSuccessListener { document ->
                Log.i("Contact Information Retrieved", document.toString())
                val contactInformation = document.toObject(ContactInformation::class.java)!!
                account.contactInformation = contactInformation
                getAccountEmailAddress(account)
            }
            .addOnFailureListener {  e ->
                Log.e("Account Error", e.message.toString())
                firebaseInterface.hideProgressDialog()
                firebaseInterface.getAccountFailed()
            }
    }


    private fun getAccountEmailAddress(account: Account){
        firebaseInterface as FirebaseAccountInterface

        fireStore.collection(IntentBundles.CollectionContactInformation).document(account.contactInformationID)
            .collection(IntentBundles.CollectionEmail).document(account.contactInformationID).get()
            .addOnSuccessListener { document ->
                Log.i("Email Retrieved", document.toString())
                account.contactInformation!!.emailAddress = document.toObject(EmailAddress::class.java)!!
                firebaseInterface.hideProgressDialog()
                firebaseInterface.getAccountSuccess(account)
            }
            .addOnFailureListener {  e ->
                Log.e("Email Error", e.message.toString())
                firebaseInterface.hideProgressDialog()
                firebaseInterface.getAccountFailed()
            }
    }

    fun getCurrentUserID(): String{
        val currentUser = auth.currentUser
        return currentUser?.uid ?: ""
    }
}