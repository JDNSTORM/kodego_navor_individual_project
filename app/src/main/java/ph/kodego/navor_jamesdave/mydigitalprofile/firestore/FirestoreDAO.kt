package ph.kodego.navor_jamesdave.mydigitalprofile.firestore

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface FirestoreDAO {
    fun newDocument(): DocumentReference
    suspend fun getDocument(documentID: String): DocumentSnapshot?
    suspend fun getDocuments(): List<DocumentSnapshot>?
    suspend fun update(documentID: String, fields: HashMap<String, Any?>): Boolean
    suspend fun delete(documentID: String): Boolean
}

abstract class FirestoreDAOImpl(): FirestoreDAO {
    protected val fireStore = FirebaseFirestore.getInstance()
    abstract val collection: String
    private val reference: CollectionReference get() = getCollectionReference()

    open fun getCollectionReference(): CollectionReference{
        return fireStore.collection(collection)
    }

    override fun newDocument(): DocumentReference = reference.document()

    override suspend fun getDocument(documentID: String): DocumentSnapshot? {
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

    override suspend fun getDocuments(): List<DocumentSnapshot>? {
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

    override suspend fun update(documentID: String, fields: HashMap<String, Any?>): Boolean {
        val task = reference.document(documentID).update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun delete(documentID: String): Boolean {
        val task = reference.document(documentID).delete()
        task.await()
        return task.isSuccessful
    }
}