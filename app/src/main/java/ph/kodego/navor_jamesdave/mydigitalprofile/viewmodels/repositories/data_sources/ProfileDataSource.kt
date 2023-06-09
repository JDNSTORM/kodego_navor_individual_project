package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources

import kotlinx.coroutines.flow.Flow
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.ProfileDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import javax.inject.Inject

class ProfileDataSource @Inject constructor(private val dao: ProfileDAOImpl) {
    suspend fun addProfile(profile: Profile): Boolean = dao.addDocument(profile)
    fun readProfile(profileID: String): Flow<Profile?> = dao.readProfile(profileID)
    fun readProfiles(): Flow<List<Profile>> = dao.readProfiles()
    fun readProfilesGroup(): Flow<List<Profile>> = dao.readProfilesGroup()
    fun readPublicProfiles(): Flow<List<Profile>> = dao.readPublicProfiles()
    suspend fun updateProfile(profile: Profile, fields: Map<String, Any?>) = dao.updateDocument(profile.profileID, fields)
    suspend fun deleteProfile(profile: Profile): Boolean = dao.deleteDocument(profile.profileID)
}