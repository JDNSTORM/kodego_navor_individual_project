package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.models.FirestoreModel
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile

interface FirestoreDAO {
    suspend fun add(model: FirestoreModel): Boolean
    suspend fun get(): List<DocumentSnapshot>?
    suspend fun get(documentID: String): DocumentSnapshot?
    suspend fun update(model: FirestoreModel, fields: HashMap<String, Any?>): Boolean
    suspend fun delete(model: FirestoreModel): Boolean
}

abstract class FirestoreDAOImpl(internal val collection: String): FirestoreDAO{
    internal val fireStore = FirebaseFirestore.getInstance()
    open val reference: CollectionReference = fireStore.collection(collection)

    override suspend fun add(model: FirestoreModel): Boolean {
        val document = reference.document()
        model.documentID = document.id
        val task = document.set(model)
        task.await()
        return if (task.isSuccessful){
            Log.i(collection, "Document Added")
            true
        }else{
            Log.e(collection, task.exception!!.message.toString())
            false
        }
    }

    override suspend fun get(): List<DocumentSnapshot>? {
        val task = reference.get()
        task.await()
        return if (task.isSuccessful && task.result.documents.isNotEmpty()){
            val documents = task.result.documents
            Log.i(collection, documents.toString())
            documents
        }else{
            Log.e(collection, task.exception?.message.toString())
            null
        }
    }

    override suspend fun get(documentID: String): DocumentSnapshot? {
        val task = reference.document(documentID).get()
        task.await()
        return if (task.isSuccessful && task.result.data != null){
            val document = task.result
            Log.i(collection, document.toString())
            document
        }else{
            Log.e(collection, task.exception?.message.toString())
            null
        }
    }

    override suspend fun update(model: FirestoreModel, fields: HashMap<String, Any?>): Boolean {
        val task = reference.document(model.documentID).update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun delete(model: FirestoreModel): Boolean {
        val task = reference.document(model.documentID).delete()
        task.await()
        return task.isSuccessful
    }
}

interface ProfessionalSummaryDAO{
    suspend fun getProfessionalSummary(): ProfessionalSummary?
}

class ProfessionalSummaryDAOImpl(profile: Profile, context: Context): FirestoreDAOImpl(FirebaseCollections.ProfessionalSummary), ProfessionalSummaryDAO{
    private val parentReference = fireStore.collection(FirebaseCollections.Profile).document(profile.profileID)
    override val reference: CollectionReference
        get() = parentReference.collection(collection)
    override suspend fun getProfessionalSummary(): ProfessionalSummary? {
        val documents = get()

        return if (documents != null){
            documents[0].toObject(ProfessionalSummary::class.java)
        }else{
            null
        }
    }
}