package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.EmailAddress
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

interface FirebaseContactInformationDAO {
    suspend fun addContactInformation(contactInformation: ContactInformation): Boolean
    suspend fun registerContactInformation(contactInformation: ContactInformation): Boolean
    suspend fun getContactInformation(contactInformationID: String): ContactInformation?
    suspend fun addEmail(emailAddress: EmailAddress): Boolean //TODO: Separate DAO?
    suspend fun registerEmail(emailAddress: EmailAddress)
    suspend fun getEmail(contactInformation: ContactInformation): EmailAddress?
}

class FirebaseContactInformationDAOImpl(): FirebaseContactInformationDAO{
    private val collection = FirebaseCollections.ContactInformation
    private val fireStore = FirebaseFirestore.getInstance()
    override suspend fun addContactInformation(contactInformation: ContactInformation): Boolean { //TODO: Probably change parameter
        val reference = fireStore
            .collection(collection)
            .document()
        contactInformation.contactInformationID = reference.id
        val task = reference.set(contactInformation, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("Contact Registration", "Successful")
            true
        }else{
            Log.e("Contact Registration", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun registerContactInformation(contactInformation: ContactInformation): Boolean {
        return if (addContactInformation(contactInformation)){ //TODO: Contents
            if (contactInformation.emailAddress != null) {
                contactInformation.emailAddress!!.contactInformationID = contactInformation.contactInformationID
                addEmail(contactInformation.emailAddress!!)
            }
            true
        }else{
            false
        }
    }

    override suspend fun getContactInformation(contactInformationID: String): ContactInformation? {
        val task = fireStore
            .collection(Constants.CollectionContactInformation)
            .document(contactInformationID)
            .get()
        task.await()
        return if (task.isSuccessful && task.result.data != null){
            Log.i("Contact Information", task.result.toString())
            val contactInformation = task.result.toObject(ContactInformation::class.java)!!
            Log.d("ContactInformationID", contactInformation.contactInformationID)
            contactInformation.emailAddress = getEmail(contactInformation)
            contactInformation
        }else{
            Log.e("Get Contact Information", task.exception!!.message.toString())
            null
        }
    }

    override suspend fun addEmail(emailAddress: EmailAddress): Boolean {
        val reference = fireStore
            .collection(collection)
            .document(emailAddress.contactInformationID)
            .collection(FirebaseCollections.Email)
            .document(emailAddress.contactInformationID)
        val emailReference = fireStore.collection(Constants.CollectionEmail).document()
        emailAddress.id = emailReference.id
        val task = reference.set(emailAddress, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("Email Registration", "Successful")
            true
        }else{
            Log.e("Email Registration", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun registerEmail(emailAddress: EmailAddress) {
        TODO("Not yet implemented")
    }

    override suspend fun getEmail(contactInformation: ContactInformation): EmailAddress? {
        val subCollection = Constants.CollectionEmail
        val task = fireStore.collection(collection).document(contactInformation.contactInformationID)
            .collection(subCollection).document(contactInformation.contactInformationID).get()
        task.await()
        return if (task.isSuccessful && task.result.data != null){
            Log.i("Email", task.result.toString())
            task.result.toObject(EmailAddress::class.java)!!
        }else{
            Log.e("Get Email", task.exception!!.message.toString())
            null
        }
    }

}