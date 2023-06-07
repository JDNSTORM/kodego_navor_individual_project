package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.dataObjects
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

interface FirestoreDAO<Model: Any> {
    suspend fun addDocument(model: Model): Boolean
    suspend fun addDocument(documentID: String, model: Model): Boolean
    suspend fun getDocument(): DocumentSnapshot?
    suspend fun getDocument(documentID: String): DocumentSnapshot?
    fun readDocument(documentID: String): Flow<DocumentSnapshot>
    suspend fun getDocuments(): List<DocumentSnapshot>?
    fun readQuery(): Flow<QuerySnapshot>
    suspend fun getGroupDocuments(): List<DocumentSnapshot>?
    fun readGroupQuery(): Flow<QuerySnapshot>
    suspend fun updateDocument(documentID: String, fields: Map<String, Any?>): Boolean
    suspend fun deleteDocument(documentID: String): Boolean
}

abstract class FirestoreDAOImpl<Model: Any>(): FirestoreDAO<Model> {
    protected val db = FirebaseFirestore.getInstance()
    protected val collection: String get() = getCollectionName()
    protected val reference: CollectionReference get() = getCollectionReference()
    protected val groupReference by lazy { db.collectionGroup(collection) }

    abstract fun getCollectionName(): String

    /**
     * Value of reference.
     * Refers to the Collection Reference Directory of the Document.
     * Must be overridden in case you are referring to a subCollection.
     * To override, you must use the Database $db then set the directory by Collection and Document
     * Example: db.collection(collectionName).document(documentID).collection($collection)
     */
    open fun getCollectionReference(): CollectionReference = db.collection(collection)

    override suspend fun addDocument(model: Model): Boolean {
        val task = reference
            .document()
            .set(model, SetOptions.merge())
        task.await()
        return task.isSuccessful
    }

    override suspend fun addDocument(documentID: String, model: Model): Boolean {
        val task = db
            .collection(collection)
            .document(documentID)
            .set(model, SetOptions.merge())
        task.await()
        return task.isSuccessful
    }

    override suspend fun getDocument(): DocumentSnapshot? {
        val task = reference.get()
        task.await()
        return if (task.isSuccessful && task.result.documents.isNotEmpty()){
            val documents = task.result.documents
            Log.i(collection, documents.toString())
            documents[0]
        }else{
            Log.e(collection, task.exception?.message.toString())
            null
        }
    }

    override suspend fun getDocument(documentID: String): DocumentSnapshot? {
        val task = reference.document(documentID).get()
        task.await()
        return if (task.isSuccessful && !task.result.data.isNullOrEmpty()){
            val document = task.result
            Log.i(collection, document.toString())
            document
        }else{
            Log.e(collection, task.exception?.message.toString())
            null
        }
    }

    override fun readDocument(documentID: String): Flow<DocumentSnapshot> {
        return reference.document(documentID).snapshots()
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

    override fun readQuery(): Flow<QuerySnapshot> {
        return reference.snapshots()
    }

    override suspend fun getGroupDocuments(): List<DocumentSnapshot>? {
        val task = groupReference.get()
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

    override fun readGroupQuery(): Flow<QuerySnapshot> {
        return groupReference.snapshots()
    }

    override suspend fun updateDocument(documentID: String, fields: Map<String, Any?>): Boolean {
        val task = reference.document(documentID).update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteDocument(documentID: String): Boolean {
        val task = reference.document(documentID).delete()
        task.await()
        return task.isSuccessful
    }

    /**
     * Retrieves the DocumentSnapshot then returns null or Object of type Model.
     * For smooth execution, implement a function to use this function.
     * Example: fun getData(documentID: String): Data? = getModel(documentID)
     * @param Model Any Class used for storing data. Preferably Data Class.
     * @return Null or an Object of type Model
     */
    protected suspend inline fun <reified Model: Any> getModel(documentID: String): Model?{
        val document = getDocument(documentID)
        return document?.toObject(Model::class.java)
    }

    /**
     * Returns a Flow of the DocumentReference which then returns null or Object of type Model.
     * For smooth execution, implement a function to use this function.
     * Example: fun readData(documentID: String): Flow<Data?> = readModel(documentID)
     * @param Model Any Class used for storing data. Preferably Data Class.
     * @return Flow of type Null or Object of type Model
     */
    protected inline fun <reified Model: Any> readModel(documentID: String): Flow<Model?>{
        return reference.document(documentID).dataObjects()
    }

    /**
     * Returns a Flow of the CollectionReference which then returns a List of Object of type Model.
     * For smooth execution, implement a function to use this function.
     * Example: fun readDataSet(documentID: String): Flow<List<Data>> = readModels(documentID)
     * For results with filters, please use $reference.
     * @param Model Any Class used for storing data. Preferably Data Class.
     * @return Flow of List of Object of type Model
     */
    protected inline fun <reified Model: Any> readModels(): Flow<List<Model>> {
        return reference.dataObjects()
    }

    /**
     * Returns a Flow of the CollectionGroup which then returns a List of Object of type Model.
     * For smooth execution, implement a function to use this function.
     * Example: fun readDataGroup(documentID: String): Flow<List<Data>> = readGroup(documentID)
     * For results with filters, please use $groupReference.
     * @param Model Any Class used for storing data. Preferably Data Class.
     * @return Flow of List of Object of type Model
     */
    protected inline fun <reified Model: Any> readGroup(): Flow<List<Model>>{
        return groupReference.dataObjects()
    }
}