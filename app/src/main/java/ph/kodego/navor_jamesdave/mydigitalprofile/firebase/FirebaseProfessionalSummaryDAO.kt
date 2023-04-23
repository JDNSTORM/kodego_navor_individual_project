package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile

interface FirebaseProfessionalSummaryDAO {
    suspend fun addProfessionalSummary(professionalSummary: ProfessionalSummary): Boolean
    suspend fun getProfessionalSummary(): ProfessionalSummary?
    suspend fun updateProfessionalSummary(professionalSummary: ProfessionalSummary, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteProfessionalSummary(professionalSummary: ProfessionalSummary): Boolean
}

class FirebaseProfessionalSummaryDAOImpl(profile: Profile, context: Context): FirebaseProfileDAOImpl(context), FirebaseProfessionalSummaryDAO{
    private val collectionAccounts = FirebaseCollections.Accounts
    private val collectionProfiles = FirebaseCollections.Profile
    private val collection = FirebaseCollections.ProfessionalSummary
    private val reference = fireStore.collection(collectionAccounts)
        .document(profile.uID).collection(collectionProfiles)
        .document(profile.profileID).collection(collection)

    override suspend fun addProfessionalSummary(professionalSummary: ProfessionalSummary): Boolean {
        val document = reference.document()
        professionalSummary.id = document.id
        val task = document.set(professionalSummary, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("Add Summary", "Successful")
            true
        }else{
            Log.e("Add Summary", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getProfessionalSummary(): ProfessionalSummary? {
        val task = reference.get()
        task.await()
        return if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("Summary", task.result.documents.toString())
            task.result.documents[0].toObject(ProfessionalSummary::class.java)!!
        }else{
            Log.e("Get Summary", task.exception?.message.toString())
            null
        }
    }

    override suspend fun updateProfessionalSummary(
        professionalSummary: ProfessionalSummary,
        fields: HashMap<String, Any?>
    ): Boolean {
        val task = reference
            .document(professionalSummary.id)
            .update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteProfessionalSummary(professionalSummary: ProfessionalSummary): Boolean {
        val task = reference
            .document(professionalSummary.id)
            .delete()
        task.await()
        return task.isSuccessful
    }

}