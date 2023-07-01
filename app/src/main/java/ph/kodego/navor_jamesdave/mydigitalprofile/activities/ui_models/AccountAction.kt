package ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models

import android.net.Uri
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account

sealed class AccountAction{
    data class SignIn(val email: String, val password: String): AccountAction()
    data class SignUp(
        val firstName: String,
        val lastName: String,
        val email: String,
        val password: String
    ): AccountAction()
    object SignOut: AccountAction()
    data class Update(val changes: Map<String, Any?>, val image: Uri? = null): AccountAction()
    data class Delete(val account: Account): AccountAction()
    data class ChangePassword(val oldPassword: String, val password: String): AccountAction()
}
