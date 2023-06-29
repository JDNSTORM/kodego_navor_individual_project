package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources

import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.transform
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ViewedProfileState
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.ProfileDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileDataSource @Inject constructor(private val dao: ProfileDAOImpl) {
    private val _viewedProfileState: MutableStateFlow<ViewedProfileState> = MutableStateFlow(ViewedProfileState.Inactive)
    val viewedProfileState = _viewedProfileState.asStateFlow()
    var activeProfile: Flow<Profile?>? = null

    suspend fun addProfile(profile: Profile): Boolean = dao.addDocument(profile.toFirestore())
    fun readProfile(profileID: String): Flow<Profile?> = dao.readProfile(profileID)
    fun readProfile(profile: Profile): Flow<Profile?> = dao.readProfile(profile)
    fun readProfiles(): Flow<List<Profile>> = dao.readProfiles()
    fun readProfilesGroup(): Flow<List<Profile>> = dao.readProfilesGroup()
    suspend fun getPublicProfiles(lastDocument: DocumentSnapshot?, limit: Int): List<DocumentSnapshot> = dao.getPublicProfiles(lastDocument, limit)
    fun readPublicProfiles(): Flow<List<Profile>> = dao.readPublicProfiles()
    suspend fun updateProfile(profile: Profile, fields: Map<String, Any?>) = dao.updateDocument(profile.profileID, fields)
    suspend fun deleteProfile(profile: Profile): Boolean = dao.deleteDocument(profile.profileID)

    suspend fun viewProfile(profile: Profile){
        activeProfile = readProfile(profile)
        _viewedProfileState.emit(
            ViewedProfileState.Active(readProfile(profile).transform {
                it?.setAccount(profile)
            })
        )
    }

    suspend fun clearProfile(){
        _viewedProfileState.emit(ViewedProfileState.Inactive)
    }
}