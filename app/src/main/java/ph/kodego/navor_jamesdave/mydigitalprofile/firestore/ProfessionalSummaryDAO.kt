package ph.kodego.navor_jamesdave.mydigitalprofile.firestore

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCollections
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile

interface ProfessionalSummaryDAO{
    suspend fun addProfessionalSummary(summary: ProfessionalSummary): Boolean
    suspend fun getProfessionalSummary(): ProfessionalSummary?
}

class ProfessionalSummaryDAOImpl(profile: Profile, context: Context): FirestoreDAOImpl<Profile>(),
    ProfessionalSummaryDAO {
    override val collection: String
        get() = FirebaseCollections.ProfessionalSummary
    private val parentReference = db.collection(FirebaseCollections.Profile).document(profile.profileID)

    override fun getCollectionReference(): CollectionReference {
        return parentReference.collection(collection)
    }

    override suspend fun addProfessionalSummary(summary: ProfessionalSummary): Boolean {
        val document = newDocument()
        summary.id = document.id
        val task = document.set(summary)
        task.await()
        return if (task.isSuccessful){
            Log.i(collection, "Document Added")
            true
        }else{
            Log.e(collection, task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getProfessionalSummary(): ProfessionalSummary? {
        val documents = getDocuments()

        return if (documents != null){
            documents[0].toObject(ProfessionalSummary::class.java)
        }else{
            null
        }
    }
}