package ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.dao

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Profile

interface FirebaseEducationDAO {
    suspend fun addEducation(education: Education): Boolean
    suspend fun getEducation(educationID: String): Education?
    suspend fun getEducations(): ArrayList<Education>
    suspend fun updateEducation(education: Education, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteEducation(education: Education): Boolean
}

class FirebaseEducationDAOImpl(private val profile: Profile): FirebaseEducationDAO {
    private val dao = FirebaseContactInformationDAOImpl()
    private val fireStore = FirebaseFirestore.getInstance()
    private val collectionAccounts = FirebaseCollections.Accounts
    private val collectionProfiles = FirebaseCollections.Profile
    private val collection = FirebaseCollections.Education
    private val reference = fireStore.collection(collectionAccounts)
        .document(profile.uID).collection(collectionProfiles)
        .document(profile.profileID).collection(collection)

    fun profileID(): String = profile.profileID

    override suspend fun addEducation(education: Education): Boolean {
        val document = reference.document()
        if(dao.registerContactInformation(education.contactInformation!!)){
            education.contactInformationID = education.contactInformation!!.contactInformationID
        }
        education.id = document.id
        val task = document.set(education, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("Add Education", "Successful")
            true
        }else{
            Log.e("Add Education", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getEducation(educationID: String): Education? {
        TODO("Not yet implemented")
    }

    override suspend fun getEducations(): ArrayList<Education> {
        val educations: ArrayList<Education> = ArrayList()
        val task = reference
//            .whereEqualTo("profileID", profileID)
            .get()
        task.await()
        if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("Educations", task.result.documents.toString())
            task.result.documents.forEach { document ->
                Log.i("Education Data", document.data.toString())
                val education = document.toObject(Education::class.java)!!
                Log.d("Education", education.toString())
                education.contactInformation = dao.getContactInformation(education.contactInformationID)

                educations.add(education)
            }
        }else{
            Log.e("Get Careers", task.exception?.message.toString())
        }
        return educations
    }

    override suspend fun updateEducation(
        education: Education,
        fields: HashMap<String, Any?>
    ): Boolean {
        val educationUpdates: HashMap<String, Any?> = fields["Career"] as HashMap<String, Any?>
        val contactInformation = education.contactInformation!!
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
            .document(education.id)
            .update(educationUpdates)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteEducation(education: Education): Boolean {
        dao.deleteContactInformation(education.contactInformation!!)
        val task = reference
            .document(education.id)
            .delete()
        task.await()
        return task.isSuccessful
    }
}