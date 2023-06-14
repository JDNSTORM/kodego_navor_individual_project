package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.first
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val repository: ProfileRepository
): AndroidViewModel(application) {
    private val activeAccount: Flow<Account?> by lazy { repository.accountSource.activeAccount!! }
    val group: Flow<List<Profile>> = //TODO: Replace with ReadPublicProfiles
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

    fun clearActiveProfile(){
        repository.profileSource.activeProfile = null
    }

    fun readActiveProfile(): Flow<Profile?>? = repository.profileSource.activeProfile

    suspend fun addProfile(profession: String, isPublic: Boolean): Boolean {
        val profile = Profile(activeAccount.first()!!.uid, profession, isPublic)
        return repository.profileSource.addProfile(profile)
    }
    private fun readProfile(profile: Profile): Flow<Profile?> = repository.profileSource.readProfile(profile)
    private fun readProfilesGroup(): Flow<List<Profile>> = repository.profileSource.readProfilesGroup()
    fun readAccountProfiles(): Flow<List<Profile>> {
        return readProfiles().combineTransform(activeAccount){ profiles, account ->
            account?.let {
                profiles.onEach { profile -> profile.setAccount(it) }
            }
            emit(profiles)
        }
    }
    private fun readPublicProfiles(): Flow<List<Profile>> = repository.profileSource.readPublicProfiles()
    private fun readProfiles(): Flow<List<Profile>> = repository.profileSource.readProfiles()
    private fun readAccounts(): Flow<List<Account>> = repository.accountSource.readAccounts()
    private fun readAccount(uID: String) = repository.accountSource.readAccount(uID)

    suspend fun updateProfile(profile: Profile, changes: Map<String, Any?>): Boolean {
        return repository.profileSource.updateProfile(profile, changes)
    }
    suspend fun deleteProfile(profile: Profile): Boolean = repository.profileSource.deleteProfile(profile)
}