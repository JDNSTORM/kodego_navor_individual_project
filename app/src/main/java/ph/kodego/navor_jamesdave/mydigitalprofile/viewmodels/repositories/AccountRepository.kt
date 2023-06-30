package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories

import android.net.Uri
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.storage.StorageException
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.auth.FirebaseAuthDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.storage.FirebaseStorageDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.storage.FirebaseStorageDAOImpl.Companion.IMAGE_TREE
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources.AccountDataSource
import javax.inject.Inject

@ViewModelScoped
class AccountRepository @Inject constructor(
    private val auth: FirebaseAuthDAOImpl,
    val source: AccountDataSource,
    val storage: FirebaseStorageDAOImpl
) {
    suspend fun loadActiveAccount(){
        auth.currentUser()?.let {
            val account = source.readAccount(it.uid)
            source.setState(AccountState.Active(account, it.uid))
        } ?: source.setState(AccountState.Inactive)
    }

    suspend fun signOut(){
        auth.signOutUser()
        source.setState(AccountState.Inactive)
    }

    suspend fun signIn(email: String, password: String){
        source.setState(AccountState.Updating)
        try {
            auth.signInUser(email, password)
            loadActiveAccount()
        }catch (e: FirebaseAuthException){
            source.setState(AccountState.Error(e))
        }
    }

    suspend fun updateAccount(fields: Map<String, Any?>): StateFlow<RemoteState>{
        val state = MutableStateFlow(RemoteState.Waiting)
        val uid = (source.accountState.value as? AccountState.Active)?.uid
        uid?.let {
            withContext(IO) {
                val userChanges: HashMap<String, Any?> = HashMap()
                if (fields.containsKey(Account.KEY_FIRST_NAME) || fields.containsKey(Account.KEY_LAST_NAME)) {
                    userChanges[FirebaseAuthDAOImpl.USER_DISPLAY_NAME] =
                        "${fields[Account.KEY_FIRST_NAME]} ${fields[Account.KEY_LAST_NAME]}"
                }
                if (fields.containsKey(Account.KEY_IMAGE)) {
                    userChanges[FirebaseAuthDAOImpl.USER_PHOTO_URI] = fields[Account.KEY_IMAGE]
                }
                try {
                    if (userChanges.isNotEmpty()) {
                        auth.updateUser(userChanges)
                    }
                    source.updateAccount(it, fields)
                    state.emit(RemoteState.Success)
                } catch (e: Exception) {
                    state.emit(RemoteState.Failed)
                }
            }
        } ?: state.emit(RemoteState.Failed)
        return state.asStateFlow()
    }

    suspend fun signUp(credentials: AccountAction.SignUp) {
        val (firstName: String, lastName: String, email: String, password: String) = credentials
        source.setState(AccountState.Updating)
        try {
            auth.createUser(email, password)?.let {
                val account = Account(firstName, lastName, email)
                source.addAccount(it.uid, account)
                loadActiveAccount()
            } ?: source.setState(AccountState.Invalid)
        }catch (e: Exception){
            source.setState(AccountState.Error(e))
        }
    }

    suspend fun updateUserPassword(oldPassword: String, password: String){
        source.setState(AccountState.Updating)
        try {
            auth.updateUserPassword(oldPassword, password)
            loadActiveAccount()
        }catch (e: FirebaseAuthException){
            source.setState(AccountState.Error(e))
        }
    }

    suspend fun replaceImage(oldImage: String, image: Uri): String?{
        return try {
            val uri = storage.uploadFile(IMAGE_TREE, image)
            try {
                storage.deleteFile(oldImage)
            }catch (e: StorageException){
                //Do nothing
            }
            uri.toString()
        }catch (e: StorageException){
            Log.e("UploadImage", e.message.toString())
            null
        }
    }
}