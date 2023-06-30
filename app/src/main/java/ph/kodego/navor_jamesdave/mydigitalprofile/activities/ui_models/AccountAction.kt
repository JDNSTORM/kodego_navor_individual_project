package ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models

import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account

sealed class AccountAction{
    data class SignIn(val email: String, val password: String)
    data class SignUp(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
    )
    data class Create(val account: Account): AccountAction()
    data class Update(val account: Account, val changes: Map<String, Any?>): AccountAction()
    data class Delete(val account: Account): AccountAction()
}
