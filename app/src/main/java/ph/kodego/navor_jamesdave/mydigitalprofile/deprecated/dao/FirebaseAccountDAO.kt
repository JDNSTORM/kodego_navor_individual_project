package ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.dao

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.EmailAddress

interface FirebaseAccountDAO {
    /**
     * Creates an Account Document using the User's UID
     */
    suspend fun addAccount(account: Account): Boolean

    /**
     * Registers User using Email and Password then proceeds to register Account's Contact Information then lastly creates Account Document
     * @return If the registration is successful
     */
    suspend fun registerAccount(firstName: String, lastName: String, email: String, password: String): Boolean
    /**
     * Gets Account Document using the User's UID
     * @return an Account or Null of the Document doesn't exist
     */
    suspend fun getAccount(uID: String): Account?
    suspend fun updateAccount(fields: HashMap<String, Any?>): Boolean
    suspend fun deleteAccount()
}

open class FirebaseAccountDAOImpl(context: Context): FirebaseUserDAOImpl(context),
    FirebaseAccountDAO {
    private val collection = FirebaseCollections.Accounts

    override suspend fun addAccount(account: Account): Boolean {
        val task = fireStore
            .collection(collection)
            .document(account.uID)
            .set(account, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Toast.makeText(context, "Account Registration Successful", Toast.LENGTH_SHORT).show()
            true
        }else{
            Log.e("Account Registration", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun registerAccount(firstName: String, lastName: String, email: String, password: String): Boolean {
        val user = registerUser(email, password)
        return if (user != null){
            val dao = FirebaseContactInformationDAOImpl()
            val account = Account(user.uid, firstName, lastName)
            val contactInformation = ContactInformation()
            contactInformation.emailAddress = EmailAddress()
            contactInformation.emailAddress!!.email = user.email.toString()
            return if(dao.registerContactInformation(contactInformation)){
                account.contactInformationID = contactInformation.contactInformationID
                account.contactInformation = contactInformation
                addAccount(account)
            }else{
                Log.e("ContactInformation", "Failed to register")
                false
            }
        }else{
            Toast.makeText(context, "Account Registration Fail", Toast.LENGTH_LONG).show()
            false
        }
    }

    override suspend fun getAccount(uID: String): Account? {
        val task = fireStore
            .collection(collection)
            .document(uID)
            .get()
        task.await()
        Log.d("Get Account", task.result.toString(), task.exception)
        return if (task.isSuccessful && task.result.data != null){
            Log.i("Account Document Retrieved", task.result.toString())
            val account = task.result.toObject(Account::class.java)!!
            account.contactInformation = FirebaseContactInformationDAOImpl().getContactInformation(account.contactInformationID)
            return account
        }else{
            Log.e("Account Error", task.exception?.message.toString())
            null
        }
    }

    override suspend fun updateAccount(fields: HashMap<String, Any?>): Boolean {
        updateUser(fields)
        val task = fireStore
            .collection(collection)
            .document(getCurrentUserID())
            .update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteAccount() {
        val task = fireStore
            .collection(collection)
            .document(getCurrentUserID())
            .delete()
        task.await()
        Log.d("Delete Account", task.result.toString(), task.exception)
    }

}