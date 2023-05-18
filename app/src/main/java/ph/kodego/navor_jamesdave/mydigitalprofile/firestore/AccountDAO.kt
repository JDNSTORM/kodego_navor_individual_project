package ph.kodego.navor_jamesdave.mydigitalprofile.firestore

import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCollections
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account

interface AccountDAO {
    suspend fun addAccount(account: Account): Boolean
    suspend fun getAccount(uID: String): Account?
}

open class AccountDAOImpl(): FirestoreDAOImpl(), AccountDAO{
    override val collection: String
        get() = FirebaseCollections.Accounts

    override suspend fun addAccount(account: Account): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAccount(uID: String): Account? {
        TODO("Not yet implemented")
    }
}