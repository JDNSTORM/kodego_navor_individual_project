package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources

import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
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

    suspend fun addProfile(profile: Profile) = dao.addDocument(profile.toFirestore())
    fun readProfile(profileID: String): Flow<Profile?> = dao.readProfile(profileID)
    fun readProfile(profile: Profile): Flow<Profile?> = dao.readProfile(profile)
    fun readProfiles(): Flow<List<Profile>> = dao.readProfiles()
    fun readProfilesGroup(): Flow<List<Profile>> = dao.readProfilesGroup()
    suspend fun getPublicProfiles(lastDocument: DocumentSnapshot?, limit: Int): List<DocumentSnapshot> = dao.getPublicProfiles(lastDocument, limit)
    fun readPublicProfiles(): Flow<List<Profile>> = dao.readPublicProfiles()
//    suspend fun updateProfile(profile: Profile, fields: Map<String, Any?>) = dao.updateDocument(profile.profileID, fields)
//    suspend fun deleteProfile(profile: Profile) = dao.deleteDocument(profile.profileID)

    suspend fun viewProfile(profile: Profile?){
        _viewedProfileState.emit(
            profile?.let {
                ViewedProfileState.Active(
                    readProfile(profile).map {
                        it?.setAccount(profile)
                        it
                    },
                    profile.refUID,
                    profile.profileID
                )
            } ?: ViewedProfileState.Invalid
        )
    }

    suspend fun clearProfile(){
        _viewedProfileState.emit(ViewedProfileState.Inactive)
    }

    fun updateProfile(profile: Profile?, fields: Map<String, Any?>): StateFlow<RemoteState>{
        val state = MutableStateFlow(RemoteState.Waiting)
        CoroutineScope(IO).launch {
            try {
                val profileID = profile?.profileID
                    ?: (viewedProfileState.value as? ViewedProfileState.Active)?.profileID
                profileID?.let {
                    dao.updateDocument(it, fields)
                    state.emit(RemoteState.Success)
                } ?: state.emit(RemoteState.Invalid)
            } catch (e: Exception){
                state.emit(RemoteState.Failed)
            }
            delay(100)
            state.emit(RemoteState.Idle)
        }
        return state.asStateFlow()
    }

    fun deleteProfile(profile: Profile): StateFlow<RemoteState>{
        val state = MutableStateFlow(RemoteState.Waiting)
        CoroutineScope(IO).launch {
            try {
                dao.deleteDocument(profile.profileID)
                state.emit(RemoteState.Success)
            } catch (e: Exception){
                state.emit(RemoteState.Failed)
            }
            delay(100)
            state.emit(RemoteState.Idle)
        }
        return state.asStateFlow()
    }
}