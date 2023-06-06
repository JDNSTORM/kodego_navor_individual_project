package ph.kodego.navor_jamesdave.mydigitalprofile.firestore

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCollections
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account

interface AccountDAO {
    suspend fun addAccount(account: Account): Boolean
    suspend fun getAccount(uID: String): Account?
}

open class AccountDAOImpl(): FirestoreDAOImpl<Account>(), AccountDAO{
    override val collection: String
        get() = FirebaseCollections.Accounts

    override suspend fun addAccount(account: Account): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAccount(uID: String): Account? {
        Log.d("AccountDAO", collection)
        val document = getDocument(uID)
        Log.d("AccountDocument", document?.data.toString())
        return document?.toObject(Account::class.java)
    }
}