package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val repository: ProfileRepository
): AndroidViewModel(application) {
    private lateinit var activeProfile: Flow<Profile?>
    private lateinit var activeAccount: Account //TODO
    val group: Flow<List<Profile>> = //TODO: Replace with ReadPublicProfiles
        readProfilesGroup().combineTransform(readAccounts()){ profiles, accounts ->
            val group: ArrayList<Profile> = ArrayList()
            for(profile in profiles){
                accounts.find { it.uID == profile.uID }?.let {
                    profile.setAccount(it)
                    group.add(profile)
                }
            }
            emit(group)
        }

    fun setActiveProfile(profile: Profile){
        activeProfile = readProfile(profile)
    }

    fun readActiveProfile(): Flow<Profile?>?{
        if(!this::activeProfile.isInitialized) return null
        return activeProfile
    }

    suspend fun addProfile(profession: String, isPublic: Boolean): Boolean {
        val profile = Profile(activeAccount.uID, profession, isPublic)
        return repository.profileSource.addProfile(profile)
    }
    private fun readProfile(profile: Profile): Flow<Profile?> = repository.profileSource.readProfile(profile.profileID)
    private fun readProfilesGroup(): Flow<List<Profile>> = repository.profileSource.readProfilesGroup()
    fun readAccountProfiles(): Flow<List<Profile>> = repository.profileSource.readProfiles()
    private fun readPublicProfiles(): Flow<List<Profile>> = repository.profileSource.readPublicProfiles()
    private fun readAccounts(): Flow<List<Account>> = repository.accountSource.readAccounts()
}