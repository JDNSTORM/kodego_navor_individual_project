package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_IMAGE
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.AccountRepository
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(private val repository: AccountRepository): ViewModel() {
    val accountState = repository.source.accountState
    val action: (AccountAction) -> StateFlow<RemoteState>?

    init {
        val actionStateFlow = MutableSharedFlow<AccountAction>()
        val signInAction = actionStateFlow.filterIsInstance<AccountAction.SignIn>()
        val signUpAction = actionStateFlow.filterIsInstance<AccountAction.SignUp>()
        val signOutAction = actionStateFlow.filterIsInstance<AccountAction.SignOut>()
        val deleteAction = actionStateFlow.filterIsInstance<AccountAction.Delete>()
        val changePasswordAction = actionStateFlow.filterIsInstance<AccountAction.ChangePassword>()

        viewModelScope.launch(IO) {
            signInAction.collect {(email, password) ->
                repository.signIn(email, password)
            }
        }
        viewModelScope.launch(IO) {
            signUpAction.collect {
                repository.signUp(it)
            }
        }
        viewModelScope.launch(IO) {
            signOutAction.collect {
                repository.signOut()
            }
        }
        viewModelScope.launch(IO) {
            deleteAction.collect{
                TODO("Feature not available")
            }
        }
        viewModelScope.launch(IO) {
            changePasswordAction.collect{ (oldPassword, newPassword) ->
                repository.updateUserPassword(oldPassword, newPassword)
            }
        }

        action = {
            when(it) {
                is AccountAction.Update -> repository.updateAccount(it.changes, it.image)
                else -> {
                    viewModelScope.launch { actionStateFlow.emit(it) }
                    null
                }
            }
        }

        repository.loadActiveAccount()
    }
}