package ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models

import kotlinx.coroutines.flow.Flow
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account

sealed class AccountState{
    object Loading: AccountState()
    data class Active(val account: Flow<Account?>): AccountState()
    object Inactive: AccountState()
    data class Error(val error: Throwable): AccountState()
}
