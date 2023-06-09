package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources

import kotlinx.coroutines.flow.Flow
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.AccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import javax.inject.Inject

class AccountDataSource @Inject constructor(private val dao: AccountDAOImpl) {
    suspend fun addAccount(uID: String, account: Account): Boolean = dao.addAccount(uID, account)
    fun readAccount(uID: String): Flow<Account?> = dao.readAccount(uID)
    fun readAccounts(): Flow<List<Account>> = dao.readAccounts()
    suspend fun updateAccount(uID: String, fields: Map<String, Any?>): Boolean = dao.updateDocument(uID, fields)
}