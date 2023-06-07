package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.auth.FirebaseAuthDAOImpl.Companion.USER_DISPLAY_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.auth.FirebaseAuthDAOImpl.Companion.USER_PHOTO_URI
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_FIRST_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_IMAGE
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_LAST_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.AccountRepository
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(app: Application, private val repository: AccountRepository): AndroidViewModel(app) {
    private lateinit var account: Flow<Account?>
    fun readAccount(uID: String): Flow<Account?> {
        account = repository.source.readAccount(uID)
        return account
    }
    private suspend fun addAccount(uID: String, account: Account): Boolean {
        return repository.source.addAccount(uID, account)
    }

    private suspend fun registerUser(email: String, password: String): FirebaseUser?{
        return repository.auth.createUser(email, password)
    }

    private suspend fun updateUser(fields: Map<String, Any?>) = repository.auth.updateUser(fields)

    suspend fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Boolean {
        val user = registerUser(email, password)
        return user?.let {
            val account = Account(firstName, lastName, email)
            addAccount(it.uid, account)
        } ?: false
    }

    fun signOut() = repository.auth.signOutUser()
    suspend fun signIn(email: String, password: String) = repository.auth.signInUser(email, password)

    suspend fun updateAccount(fields: Map<String, Any?>): Boolean{
        val account = account.single() ?: return false
        val userChanges: HashMap<String, Any?> = HashMap()
        if (fields.containsKey(KEY_FIRST_NAME) || fields.containsKey(KEY_LAST_NAME)){
            userChanges[USER_DISPLAY_NAME] = account.displayName()
        }
        if (fields.containsKey(KEY_IMAGE)){
            userChanges[USER_PHOTO_URI] = fields[KEY_IMAGE]
        }
        if (userChanges.isNotEmpty()){
            updateUser(userChanges)
        }
        return updateAccount(account.uID, fields)
    }

    private suspend fun updateAccount(uID: String, fields: Map<String, Any?>) = repository.source.updateAccount(uID, fields)
}