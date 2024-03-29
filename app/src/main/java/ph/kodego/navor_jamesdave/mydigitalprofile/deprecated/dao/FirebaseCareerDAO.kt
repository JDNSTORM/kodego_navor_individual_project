package ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Profile

interface FirebaseCareerDAO {
    suspend fun addCareer(career: Career): Boolean
    suspend fun getCareer(careerID: String): Career?
    suspend fun getCareers(): ArrayList<Career>
    suspend fun updateCareer(career: Career, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteCareer(career: Career): Boolean
}

class FirebaseCareerDAOImpl(private val profile: Profile): FirebaseCareerDAO {
    private val dao = FirebaseContactInformationDAOImpl()
    private val fireStore = FirebaseFirestore.getInstance()
    private val collectionAccounts = FirebaseCollections.Accounts
    private val collectionProfiles = FirebaseCollections.Profile
    private val collection = FirebaseCollections.Career
    private val reference = fireStore.collection(collectionAccounts)
        .document(profile.uID).collection(collectionProfiles)
        .document(profile.profileID).collection(collection)

    fun profileID(): String = profile.profileID

    override suspend fun addCareer(career: Career): Boolean {
        val document = reference.document()
        if(dao.registerContactInformation(career.contactInformation!!)){
            career.contactInformationID = career.contactInformation!!.contactInformationID
        }
        career.id = document.id
        val task = document.set(career, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("Add Career", "Successful")
            true
        }else{
            Log.e("Add Career", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getCareer(careerID: String): Career? {
        TODO("Not yet implemented")
    }

    override suspend fun getCareers(): ArrayList<Career> {
        val careers: ArrayList<Career> = ArrayList()
        val task = reference
//            .whereEqualTo("profileID", profileID)
            .get()
        task.await()
        if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("Careers", task.result.documents.toString())
            task.result.documents.forEach { document ->
                Log.i("Career Data", document.data.toString())
                val career = document.toObject(Career::class.java)!!
                Log.d("Career", career.toString())
                career.contactInformation = dao.getContactInformation(career.contactInformationID)

                careers.add(career)
            }
        }else{
            Log.e("Get Careers", task.exception?.message.toString())
        }
        return careers
    }

    override suspend fun updateCareer(career: Career, fields: HashMap<String, Any?>): Boolean { //TODO!!!: Optimize
        val careerUpdates: HashMap<String, Any?> = fields["Career"] as HashMap<String, Any?>
        val contactInformation = career.contactInformation!!
        if (fields.containsKey("Address")){
            dao.updateAddress(
                contactInformation.address!!,
                fields["Address"] as HashMap<String, Any?>
            )
        }
        if (fields.containsKey("Website")){
            dao.updateWebsite(
                contactInformation.website!!,
                fields["Website"] as HashMap<String, Any?>
            )
        }
        if (fields.containsKey("ContactNumber")){
            dao.updateContactNumber(
                contactInformation.contactNumber!!,
                fields["ContactNumber"] as HashMap<String, Any?>
            )
        }
        val task = reference
            .document(career.id)
            .update(careerUpdates)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteCareer(career: Career): Boolean {
        dao.deleteContactInformation(career.contactInformation!!)
        val task = reference
            .document(career.id)
            .delete()
        task.await()
        return task.isSuccessful
    }
}