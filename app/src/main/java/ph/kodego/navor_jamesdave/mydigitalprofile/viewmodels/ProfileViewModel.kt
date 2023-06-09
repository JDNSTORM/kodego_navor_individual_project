package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val repository: ProfileRepository
): AndroidViewModel(application) {
    private lateinit var activeProfile: Flow<Profile?>
    val group: Flow<List<Profile>> =
        repository
        .profileSource
        .readProfilesGroup().combineTransform(
            repository.accountSource.readAccounts()
        ){ profiles, accounts ->
            val group: ArrayList<Profile> = ArrayList()
            for(profile in profiles){
                accounts.find { it.uID == profile.uID }?.let {
                    profile.setAccount(it)
                    group.add(profile)
                }
            }
            emit(group)
        }

    private suspend fun combineProfilesAndAccounts(){

    }
}