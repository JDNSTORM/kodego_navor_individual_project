package ph.kodego.navor_jamesdave.mydigitalprofile.firestore

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

interface FirestoreDAO<Model> {

    suspend fun addDocument(model: Model): Boolean
    fun newDocument(): DocumentReference
    suspend fun getDocument(documentID: String): DocumentSnapshot?
    suspend fun getDocuments(): List<DocumentSnapshot>?
    suspend fun getAllDocuments(): List<DocumentSnapshot>?
    suspend fun updateDocument(documentID: String, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteDocument(documentID: String): Boolean
}

abstract class FirestoreDAOImpl<Model : Any>(): FirestoreDAO<Model> {
    protected val db = FirebaseFirestore.getInstance()
    abstract val collection: String
    private val reference: CollectionReference get() = getCollectionReference()
    override suspend fun addDocument(model: Model): Boolean {
        val task = db
            .collection(collection)
            .document()
            .set(model, SetOptions.merge())
        task.await()
        return task.isSuccessful
    }

    open fun getCollectionReference(): CollectionReference{
        return db.collection(collection)
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

    override suspend fun getAllDocuments(): List<DocumentSnapshot>? {
        val task = db.collectionGroup(collection).get()
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

    override suspend fun updateDocument(documentID: String, fields: HashMap<String, Any?>): Boolean {
        val task = reference.document(documentID).update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteDocument(documentID: String): Boolean {
        val task = reference.document(documentID).delete()
        task.await()
        return task.isSuccessful
    }

    suspend inline fun <reified T: Model> getModel(documentID: String): Model? {
        val document = getDocument(documentID)
        return document?.let {
            document.toObject(T::class.java)
        }
    }
}