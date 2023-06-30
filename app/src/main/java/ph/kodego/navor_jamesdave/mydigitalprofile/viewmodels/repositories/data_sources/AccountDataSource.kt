package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.AccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountDataSource @Inject constructor(private val dao: AccountDAOImpl) {
    private val _accountState: MutableStateFlow<AccountState> = MutableStateFlow(AccountState.Inactive)
    val accountState = _accountState.asStateFlow()

    var activeAccount: Flow<Account?>? = null
    suspend fun addAccount(uID: String, account: Account) = dao.addAccount(uID, account)
    suspend fun getAccount(uID: String): Account? = dao.getAccount(uID)
    fun readAccount(uID: String): Flow<Account?> = dao.readAccount(uID)
    fun readActiveAccount(uID: String): Flow<Account?>{
        activeAccount ?: kotlin.run {
            activeAccount = readAccount(uID)
        }
        return activeAccount!!
    }
    fun clearActiveAccount(){ activeAccount = null}
    fun readAccounts(): Flow<List<Account>> = dao.readAccounts()
    suspend fun updateAccount(uID: String, fields: Map<String, Any?>) = dao.updateDocument(uID, fields)

    suspend fun setState(state: AccountState){
        _accountState.emit(state)
    }
}