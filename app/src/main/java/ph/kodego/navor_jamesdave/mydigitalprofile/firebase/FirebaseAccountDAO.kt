package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.EmailAddress
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

interface FirebaseAccountDAO {
    suspend fun addAccount(account: Account): Boolean
    suspend fun registerAccount(firstName: String, lastName: String, email: String, password: String): Boolean
    suspend fun getAccount(uID: String): Account?
}

open class FirebaseAccountDAOImpl(context: Context): FirebaseUserDAOImpl(context), FirebaseAccountDAO{
    private val collection = FirebaseCollections.Accounts

    override suspend fun addAccount(account: Account): Boolean {
        val task = fireStore
            .collection(collection)
            .document(account.uID)
            .set(account, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            auth.signOut()
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
            contactInformation.emailAddress = EmailAddress(email = user.email.toString())
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
        Log.d("Get Account", task.result.toString())
        return if (task.isSuccessful){
            Log.i("Account Document Retrieved", task.result.toString())
            val account = task.result.toObject(Account::class.java)!!
            account.contactInformation = FirebaseContactInformationDAOImpl().getContactInformation(account.contactInformationID)
            return account
        }else{
            Log.e("Account Error", task.exception!!.message.toString())
            null
        }
    }

}