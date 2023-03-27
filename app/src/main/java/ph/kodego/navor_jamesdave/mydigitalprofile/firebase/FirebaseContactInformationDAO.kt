package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.EmailAddress
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

interface FirebaseContactInformationDAO {
    fun addContactInformation(contactInformation: ContactInformation): Boolean
    fun registerContactInformation(contactInformation: ContactInformation)
    fun getContactInformation(contactInformationID: String): ContactInformation
    fun addEmail(emailAddress: EmailAddress): Boolean
    fun registerEmail(emailAddress: EmailAddress)
    fun getEmail(contactInformation: ContactInformation): EmailAddress?
}

class FirebaseContactInformationDAOImpl(): FirebaseContactInformationDAO{
    private val collection = FirebaseCollections.ContactInformation
    internal val fireStore = FirebaseFirestore.getInstance()
    override fun addContactInformation(contactInformation: ContactInformation): Boolean {
        val reference = fireStore
            .collection(collection)
            .document(contactInformation.contactInformationID)
        contactInformation.contactInformationID = reference.id
        val task = reference.set(contactInformation, SetOptions.merge())
        return if (task.isSuccessful){
            Log.i("Contact Registration", "Successful")
            true
        }else{
            Log.e("Contact Registration", task.exception!!.message.toString())
            false
        }
    }

    override fun registerContactInformation(contactInformation: ContactInformation) {
        if (addContactInformation(contactInformation)){ //TODO
            if (contactInformation.emailAddress != null) {
                contactInformation.emailAddress!!.contactInformationID = contactInformation.contactInformationID
                addEmail(contactInformation.emailAddress!!)
            }
        }
    }

    override fun getContactInformation(contactInformationID: String): ContactInformation {
        TODO("Not yet implemented")
    }

    override fun addEmail(emailAddress: EmailAddress): Boolean {
        val reference = fireStore
            .collection(collection)
            .document(emailAddress.contactInformationID)
            .collection(FirebaseCollections.Email)
            .document(emailAddress.contactInformationID)
        val emailReference = fireStore.collection(Constants.CollectionEmail).document()
        emailAddress.id = emailReference.id

        val task = reference.set(emailAddress, SetOptions.merge())
        return if (task.isSuccessful){
            Log.i("Email Registration", "Successful")
            true
        }else{
            Log.e("Email Registration", task.exception!!.message.toString())
            false
        }
    }

    override fun registerEmail(emailAddress: EmailAddress) {
        TODO("Not yet implemented")
    }

    override fun getEmail(contactInformation: ContactInformation): EmailAddress? {
        TODO("Not yet implemented")
    }

}