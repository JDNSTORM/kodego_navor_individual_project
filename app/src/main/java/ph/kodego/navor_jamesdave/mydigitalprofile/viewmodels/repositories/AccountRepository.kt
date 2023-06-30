package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories

import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.scopes.ViewModelScoped
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.auth.FirebaseAuthDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.storage.FirebaseStorageDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources.AccountDataSource
import javax.inject.Inject

@ViewModelScoped
class AccountRepository @Inject constructor(
    val auth: FirebaseAuthDAOImpl,
    val source: AccountDataSource,
    val storage: FirebaseStorageDAOImpl
) {
    suspend fun getActiveAccount(){
        auth.currentUser()?.let {
            val account = source.readAccount(it.uid)
            source.setState(AccountState.Active(account, it.uid))
        } ?: source.setState(AccountState.Invalid)
    }

    suspend fun signOut(){
        auth.signOutUser()
        source.setState(AccountState.Inactive)
    }

    suspend fun signIn(email: String, password: String){
        try {
            auth.signInUser(email, password)
            getActiveAccount()
        }catch (e: FirebaseAuthException){
            source.setState(AccountState.Error(e))
        }
    }
}