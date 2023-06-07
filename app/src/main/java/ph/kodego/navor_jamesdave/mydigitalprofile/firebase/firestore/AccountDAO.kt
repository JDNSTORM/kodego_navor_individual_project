package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore

import kotlinx.coroutines.flow.Flow
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.FirestoreCollections.ACCOUNT_COLLECTION
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account

interface AccountDAO{
    suspend fun addAccount(uID: String, account: Account): Boolean
    suspend fun getAccount(uID: String): Account?
    fun readAccount(uID: String): Flow<Account?>
    fun readAccounts(): Flow<List<Account>>
}

class AccountDAOImpl(): FirestoreDAOImpl<Account>(), AccountDAO {
    override fun getCollectionName(): String = ACCOUNT_COLLECTION

    override suspend fun addAccount(uID: String, account: Account): Boolean {
        return addDocument(uID, account)
    }

    override suspend fun getAccount(uID: String): Account? = getModel(uID)
    override fun readAccount(uID: String): Flow<Account?> = readModel(uID)
    override fun readAccounts(): Flow<List<Account>> = readGroup()
}