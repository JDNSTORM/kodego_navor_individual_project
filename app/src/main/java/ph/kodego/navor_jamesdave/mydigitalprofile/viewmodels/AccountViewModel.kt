package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountAction
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.auth.FirebaseAuthDAOImpl.Companion.USER_DISPLAY_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.auth.FirebaseAuthDAOImpl.Companion.USER_PHOTO_URI
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_FIRST_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_IMAGE
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_LAST_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.storage.FirebaseStorageDAOImpl.Companion.IMAGE_TREE
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.AccountRepository
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(app: Application, private val repository: AccountRepository): AndroidViewModel(app) {
    val accountState = repository.source.accountState
    val action: (AccountAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<AccountAction>()
        val signInAction = actionStateFlow.filterIsInstance<AccountAction.SignIn>()
        val signUpAction = actionStateFlow.filterIsInstance<AccountAction.SignUp>()
        val signOutAction = actionStateFlow.filterIsInstance<AccountAction.SignOut>()
        val updateAction = actionStateFlow.filterIsInstance<AccountAction.Update>()
        val deleteAction = actionStateFlow.filterIsInstance<AccountAction.Delete>()
        val changePasswordAction = actionStateFlow.filterIsInstance<AccountAction.ChangePassword>()

        viewModelScope.launch {
            signInAction.collect {(email, password) ->
                repository.signIn(email, password)
            }
        }
        viewModelScope.launch {
            signUpAction.collect {
                repository.signUp(it)
            }
        }
        viewModelScope.launch {
            signOutAction.collect {
                repository.signOut()
            }
        }
        viewModelScope.launch {
            updateAction.collect {
                val changes = it.changes.toMutableMap()
                val image = it.image?.let {  uri ->
                    repository.replaceImage(changes[KEY_IMAGE]?.toString() ?: "", uri)
                }
                image?.let {
                    changes[KEY_IMAGE] = it
                }
                repository.updateAccount(changes)
            }
        }
        viewModelScope.launch {
            deleteAction.collect{
                TODO("Feature not available")
            }
        }
        viewModelScope.launch {
            changePasswordAction.collect{ (oldPassword, newPassword) ->
                repository.updateUserPassword(oldPassword, newPassword)
            }
        }

        action = { viewModelScope.launch { actionStateFlow.emit(it) } }

        viewModelScope.launch {
            repository.loadActiveAccount()
        }
    }

//    val activeAccount: Flow<Account?> by lazy { readActiveAccount(repository.auth.currentUser()!!.uid) }
//    private fun readActiveAccount(uID: String): Flow<Account?> {
//        return repository.source.readActiveAccount(uID)
//    }
//
//    private suspend fun addAccount(uID: String, account: Account): Boolean {
//        return repository.source.addAccount(uID, account)
//    }
//
//    private suspend fun registerUser(email: String, password: String): FirebaseUser?{
//        return repository.auth.createUser(email, password)
//    }
//
//    private suspend fun updateUser(fields: Map<String, Any?>) = repository.auth.updateUser(fields)
//
//    suspend fun signUp(
//        firstName: String,
//        lastName: String,
//        email: String,
//        password: String
//    ): Boolean {
//        val user = registerUser(email, password)
//        return user?.let {
//            val account = Account(firstName, lastName, email)
//            addAccount(it.uid, account)
//        } ?: false
//    }
//
//    fun signOut() {
//        repository.source.clearActiveAccount()
//        repository.auth.signOutUser()
//    }
//    suspend fun signIn(email: String, password: String) = repository.auth.signInUser(email, password)
//
//    suspend fun updateAccount(fields: Map<String, Any?>): Boolean{
//        val account = activeAccount.first() ?: return false
//        val userChanges: HashMap<String, Any?> = HashMap()
//        if (fields.containsKey(KEY_FIRST_NAME) || fields.containsKey(KEY_LAST_NAME)){
//            userChanges[USER_DISPLAY_NAME] = account.displayName()
//        }
//        if (fields.containsKey(KEY_IMAGE)){
//            userChanges[USER_PHOTO_URI] = fields[KEY_IMAGE]
//        }
//        if (userChanges.isNotEmpty()){
//            updateUser(userChanges)
//        }
//        return updateAccount(account.uid, fields)
//    }
//
//    private suspend fun updateAccount(uID: String, fields: Map<String, Any?>) = repository.source.updateAccount(uID, fields)
//
//    suspend fun uploadImage(image: Uri): String?{
//        val account = activeAccount.first() ?: return null
//        return repository.storage.uploadFile(IMAGE_TREE, image)?.let {
//            repository.storage.deleteFile(account.image)
//            it.toString()
//        }
//    }
//
//    suspend fun updateUserPassword(oldPassword: String, password: String): Boolean{
////        return repository.auth.updateUserPassword(oldPassword, password)
//    }
}