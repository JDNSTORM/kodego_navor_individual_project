package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.dataObjects
import kotlinx.coroutines.flow.Flow
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.FirestoreCollections.ACCOUNT_COLLECTION
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.FirestoreCollections.PROFILE_COLLECTION
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

interface ProfileDAO {
    suspend fun getProfile(profileID: String): Profile?
    fun readProfile(profileID: String): Flow<Profile?>
    fun readProfiles(): Flow<List<Profile>>
    fun readProfilesGroup(): Flow<List<Profile>>
    fun readPublicProfiles(): Flow<List<Profile>>
}

class ProfileDAOImpl(): FirestoreDAOImpl<Profile>(), ProfileDAO{
    private val uID by lazy { FirebaseAuth.getInstance().currentUser!!.uid }
    override fun getCollectionName(): String = PROFILE_COLLECTION

    override fun getCollectionReference(): CollectionReference {
        return db.collection(ACCOUNT_COLLECTION).document(uID).collection(collection)
    }

    override suspend fun getProfile(profileID: String): Profile? = getProfile(profileID)
    override fun readProfile(profileID: String): Flow<Profile?> = readModel(profileID)
    override fun readProfiles(): Flow<List<Profile>> = readModels()
    override fun readProfilesGroup(): Flow<List<Profile>> = readGroup()
    override fun readPublicProfiles(): Flow<List<Profile>> {
        return groupReference.whereEqualTo("isPublic", true).dataObjects()
    }
}