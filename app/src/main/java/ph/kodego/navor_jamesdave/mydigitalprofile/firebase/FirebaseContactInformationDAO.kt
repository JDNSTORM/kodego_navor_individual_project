package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.models.EmailAddress

interface FirebaseContactInformationDAO: FirebaseEmailDAO, FirebaseAddressDAO, FirebaseContactNumberDAO {
    suspend fun addContactInformation(contactInformation: ContactInformation): Boolean
    suspend fun registerContactInformation(contactInformation: ContactInformation): Boolean
    suspend fun getContactInformation(contactInformationID: String): ContactInformation?
}
interface FirebaseEmailDAO{
    suspend fun addEmail(emailAddress: EmailAddress): Boolean
    suspend fun registerEmail(emailAddress: EmailAddress) //TODO: Not Needed?
    suspend fun getEmail(contactInformation: ContactInformation): EmailAddress?
    suspend fun deleteEmail()
}
interface FirebaseAddressDAO{
    suspend fun addAddress(address: Address): Boolean
    suspend fun getAddress(contactInformation: ContactInformation): Address?
    suspend fun updateAddress(address: Address, fields: HashMap<String, Any?>): Boolean
}

interface FirebaseContactNumberDAO{
    suspend fun addContactNumber(contactNumber: ContactNumber): Boolean
    suspend fun getContactNumber(contactInformation: ContactInformation): ContactNumber?
    suspend fun updateContactNumber(contactNumber: ContactNumber, fields: HashMap<String, Any?>): Boolean
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
        Log.d("Contact Registration", task.result.toString(), task.exception)
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
            .collection(collection)
            .document(contactInformationID)
            .get()
        task.await()
        return if (task.isSuccessful && task.result.data != null){
            Log.i("Contact Information", task.result.toString())
            val contactInformation = task.result.toObject(ContactInformation::class.java)!!
            Log.d("ContactInformationID", contactInformation.contactInformationID)
            contactInformation.emailAddress = getEmail(contactInformation)
            contactInformation.address = getAddress(contactInformation)
            contactInformation.contactNumber = getContactNumber(contactInformation)
            contactInformation.website = null //TODO
            contactInformation
        }else{
            Log.e("Get Contact Information", task.exception!!.message.toString())
            null
        }
    }

    override suspend fun addEmail(emailAddress: EmailAddress): Boolean {
        val subCollection = FirebaseCollections.Email
        val reference = fireStore
            .collection(collection)
            .document(emailAddress.contactInformationID)
            .collection(subCollection)
            .document(emailAddress.contactInformationID)
        val emailReference = fireStore.collection(subCollection).document()
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
        val subCollection = FirebaseCollections.Email
        TODO("Not yet implemented")
    }

    override suspend fun getEmail(contactInformation: ContactInformation): EmailAddress? {
        val subCollection = FirebaseCollections.Email
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

    override suspend fun deleteEmail() {
        TODO("Not yet implemented")
    }

    override suspend fun addAddress(address: Address): Boolean {
        val subCollection = FirebaseCollections.Address
        val reference = fireStore
            .collection(collection)
            .document(address.contactInformationID)
            .collection(subCollection)
            .document(address.contactInformationID)
        val addressReference = fireStore.collection(subCollection).document()
        address.id = addressReference.id
        val task = reference.set(address, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("Address Registration", "Successful")
            true
        }else{
            Log.e("Address Registration", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getAddress(contactInformation: ContactInformation): Address? {
        val subCollection = FirebaseCollections.Address
        val task = fireStore.collection(collection).document(contactInformation.contactInformationID)
            .collection(subCollection).document(contactInformation.contactInformationID).get()
        task.await()
        return if (task.isSuccessful && task.result.data != null){
            Log.i("Address", task.result.toString())
            task.result.toObject(Address::class.java)!!
        }else{
            Log.e("Get Email", task.exception!!.message.toString())
            null
        }
    }

    override suspend fun updateAddress(address: Address, fields: HashMap<String, Any?>): Boolean {
        val subCollection = FirebaseCollections.Address
        val task = fireStore
            .collection(collection)
            .document(address.contactInformationID)
            .collection(subCollection)
            .document(address.contactInformationID)
            .update(fields)
        task.await()
        Log.i("Address Update", task.isSuccessful.toString())
        return task.isSuccessful
    }

    override suspend fun addContactNumber(contactNumber: ContactNumber): Boolean {
        val subCollection = FirebaseCollections.ContactNumber
        val reference = fireStore
            .collection(collection)
            .document(contactNumber.contactInformationID)
            .collection(subCollection)
            .document(contactNumber.contactInformationID)
        val addressReference = fireStore.collection(subCollection).document()
        contactNumber.id = addressReference.id
        val task = reference.set(contactNumber, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("ContactNumber Registration", "Successful")
            true
        }else{
            Log.e("ContactNumber Registration", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getContactNumber(contactInformation: ContactInformation): ContactNumber? {
        val subCollection = FirebaseCollections.ContactNumber
        val task = fireStore.collection(collection).document(contactInformation.contactInformationID)
            .collection(subCollection).document(contactInformation.contactInformationID).get()
        task.await()
        return if (task.isSuccessful && task.result.data != null){
            Log.i("ContactNumber", task.result.toString())
            task.result.toObject(ContactNumber::class.java)!!
        }else{
            Log.e("Get ContactNumber", task.exception!!.message.toString())
            null
        }
    }

    override suspend fun updateContactNumber(
        contactNumber: ContactNumber,
        fields: HashMap<String, Any?>
    ): Boolean {
        val subCollection = FirebaseCollections.ContactNumber
        val task = fireStore
            .collection(collection)
            .document(contactNumber.contactInformationID)
            .collection(subCollection)
            .document(contactNumber.contactInformationID)
            .update(fields)
        task.await()
        Log.i("ContactNumber Update", task.isSuccessful.toString())
        return task.isSuccessful
    }

}