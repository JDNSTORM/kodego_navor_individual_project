package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
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
    private val repository: ProfileRepository
): ViewModel() {
    val viewedProfileState = repository.profileSource.viewedProfileState
    val accountState = repository.accountSource.accountState
    val accountProfiles: Flow<List<Profile>>? get() = repository.getAccountProfiles()
    val action: (ProfileAction) -> StateFlow<RemoteState>?

    init {
        action = {
            when(it){
                is ProfileAction.Update -> repository.profileSource.updateProfile( it.profile, it.changes)
                is ProfileAction.Select -> {
                    viewModelScope.launch { repository.profileSource.viewProfile(it.profile) }
                    null
                }
                is ProfileAction.Create -> repository.addProfile(it.profession)
                is ProfileAction.Delete -> repository.profileSource.deleteProfile(it.profile)
            }
        }
    }
}