package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore

import kotlinx.coroutines.flow.Flow
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.FirestoreCollections.ACCOUNT_COLLECTION
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account

interface AccountDAO{
    suspend fun addAccount(uID: String, account: Account): Boolean
    suspend fun getAccount(uID: String): Account?
    fun readAccount(uID: String): Flow<Account?>
}

class AccountDAOImpl(): FirestoreDAOImpl<Account>(), AccountDAO {
    override fun getCollectionName(): String = ACCOUNT_COLLECTION

    override suspend fun addAccount(uID: String, account: Account): Boolean {
        return addDocument(uID, account)
    }

    override suspend fun getAccount(uID: String): Account? {
//        val document = getDocument(uID)
//        return document?.let {
//            val account = it.toObject(Account::class.java)
//            account?.uID = it.id
//            account
//        }
        return getModel(uID)
    }

    override fun readAccount(uID: String): Flow<Account?> {
        return readModel(uID)
    }
}