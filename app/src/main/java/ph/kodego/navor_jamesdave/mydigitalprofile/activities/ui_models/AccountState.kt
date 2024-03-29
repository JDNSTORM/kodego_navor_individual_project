package ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models

import kotlinx.coroutines.flow.Flow
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account

sealed interface AccountState{
    data class Active(val account: Flow<Account?>, val uid: String): AccountState
    object Inactive: AccountState
    object Invalid: AccountState
    object Updating: AccountState
    data class Error(val error: Throwable): AccountState
}
