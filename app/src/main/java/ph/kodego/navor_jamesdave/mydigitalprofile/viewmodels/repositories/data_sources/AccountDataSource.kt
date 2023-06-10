package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.AccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountDataSource @Inject constructor(private val dao: AccountDAOImpl) {
    var activeAccount: Flow<Account?>? = null
    suspend fun addAccount(uID: String, account: Account): Boolean = dao.addAccount(uID, account)
    fun readAccount(uID: String): Flow<Account?> = dao.readAccount(uID)
    fun readActiveAccount(uID: String): Flow<Account?>{
        activeAccount ?: kotlin.run {
            activeAccount = readAccount(uID)
        }
        return activeAccount!!
    }
    fun clearActiveAccount(){ activeAccount = null}
    fun readAccounts(): Flow<List<Account>> = dao.readAccounts()
    suspend fun updateAccount(uID: String, fields: Map<String, Any?>): Boolean = dao.updateDocument(uID, fields)
}