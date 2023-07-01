package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ProfileAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val repository: ProfileRepository
): AndroidViewModel(application) {
    val viewedProfileState = repository.profileSource.viewedProfileState
    val accountState = repository.accountSource.accountState
    val accountProfiles: Flow<List<Profile>>? get() = repository.getAccountProfiles()
    val action: (ProfileAction) -> StateFlow<RemoteState>?

    init {
        val actionStateFlow = MutableSharedFlow<ProfileAction>()
        val viewAction = actionStateFlow.filterIsInstance<ProfileAction.Select>()
        val createAction = actionStateFlow.filterIsInstance<ProfileAction.Create>()
        val updateAction = actionStateFlow.filterIsInstance<ProfileAction.Update>()
        val deleteAction = actionStateFlow.filterIsInstance<ProfileAction.Delete>()



        action = {
            when(it){
                is ProfileAction.Update -> repository.profileSource.updateProfile( it.changes)
                else -> {
                    viewModelScope.launch { actionStateFlow.emit(it) }
                    null
                }
            }
        }
    }

//    private val activeAccount: Flow<Account?> by lazy { repository.accountSource.activeAccount!! }
    val group: Flow<List<Profile>> =
        readPublicProfiles().combineTransform(readAccounts()){ profiles, accounts ->
            val group: ArrayList<Profile> = ArrayList()
            for(profile in profiles){
                accounts.find { it.uid == profile.refUID }?.let {
                    profile.setAccount(it)
                    group.add(profile)
                }
                Log.d("Profiles", profile.toString())
            }
            emit(group)
        }

    fun setActiveProfile(profile: Profile){
        val activeProfile = readProfile(profile)
        val activeAccount = readAccount(profile.refUID)
        val completeProfile = activeProfile.combineTransform(activeAccount){ profile, account ->
            if (profile != null) {
                account?.let {
                    profile.setAccount(it)
                }
            }
            Log.d("ActiveAccount", account?.displayName().toString())
            Log.d("ActiveProfile", profile?.displayName().toString())
            emit(profile)
        }
        repository.profileSource.activeProfile = completeProfile
    }

    @Deprecated("Switched to ViewedProfileState")
    fun clearActiveProfile(){
        repository.profileSource.activeProfile = null
        viewModelScope.launch {
            repository.profileSource.clearProfile()
        }
    }

    fun readActiveProfile(): Flow<Profile?>? = repository.profileSource.activeProfile

    //TODO: For Creating Profile
//    suspend fun addProfile(profession: String): Boolean {
//        val profile = Profile(activeAccount.first()!!.uid, profession)
//        return repository.profileSource.addProfile(profile)
//    }
    private fun readProfile(profile: Profile): Flow<Profile?> = repository.profileSource.readProfile(profile)
    private fun readProfilesGroup(): Flow<List<Profile>> = repository.profileSource.readProfilesGroup()
//    fun readAccountProfiles(): Flow<List<Profile>> {
//        return readProfiles().combineTransform(activeAccount){ profiles, account ->
//            account?.let {
//                profiles.onEach { profile -> profile.setAccount(it) }
//            }
//            emit(profiles)
//        }
//    }
    private fun readPublicProfiles(): Flow<List<Profile>> = repository.profileSource.readPublicProfiles()
    private fun readProfiles(): Flow<List<Profile>> = repository.profileSource.readProfiles()
    private fun readAccounts(): Flow<List<Account>> = repository.accountSource.readAccounts()
    private fun readAccount(uID: String) = repository.accountSource.readAccount(uID)

//    suspend fun updateProfile(profile: Profile, changes: Map<String, Any?>): Boolean {
//        return repository.profileSource.updateProfile(profile, changes)
//    }
//    suspend fun deleteProfile(profile: Profile): Boolean = repository.profileSource.deleteProfile(profile)
}