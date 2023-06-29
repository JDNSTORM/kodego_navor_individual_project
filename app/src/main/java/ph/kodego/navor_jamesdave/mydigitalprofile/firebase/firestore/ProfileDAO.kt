package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.dataObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.FirestoreCollections.ACCOUNT_COLLECTION
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.FirestoreCollections.PROFILE_COLLECTION
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

interface ProfileDAO {
    suspend fun getProfile(profileID: String): Profile?
    fun readProfile(profileID: String): Flow<Profile?>
    fun readProfile(profile: Profile): Flow<Profile?>
    fun readProfiles(): Flow<List<Profile>>
    fun readProfilesGroup(): Flow<List<Profile>>
    suspend fun getPublicProfiles(lastDocument: DocumentSnapshot?, limit: Long): List<DocumentSnapshot>
    fun readPublicProfiles(): Flow<List<Profile>>
}

class ProfileDAOImpl(): FirestoreDAOImpl(), ProfileDAO{
    private val uid get() =  FirebaseAuth.getInstance().currentUser!!.uid
    override fun getCollectionName(): String = PROFILE_COLLECTION

    override fun getCollectionReference(): CollectionReference {
        return db.collection(ACCOUNT_COLLECTION).document(uid).collection(collection)
    }

    override suspend fun getProfile(profileID: String): Profile? = getProfile(profileID)
    override fun readProfile(profileID: String): Flow<Profile?> = readModel(profileID)
    override fun readProfile(profile: Profile): Flow<Profile?> {
        return db.collection(ACCOUNT_COLLECTION).document(profile.refUID)
            .collection(collection).document(profile.profileID).dataObjects()
    }

    override fun readProfiles(): Flow<List<Profile>> = readModels()
    override fun readProfilesGroup(): Flow<List<Profile>> = readGroup()
    override suspend fun getPublicProfiles(
        lastDocument: DocumentSnapshot?,
        limit: Long
    ): List<DocumentSnapshot> {
        return groupReference.whereEqualTo(Profile.KEY_IS_PUBLIC, true).also { query ->
            lastDocument?.let { query.startAfter(it) } ?: query
        }.get().await().documents
    }

    override fun readPublicProfiles(): Flow<List<Profile>> {
        return groupReference.whereEqualTo(Profile.KEY_IS_PUBLIC, true).dataObjects()
    }
}