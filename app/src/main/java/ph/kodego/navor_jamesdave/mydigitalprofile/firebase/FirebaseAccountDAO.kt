package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

interface FirebaseAccountDAO {
    fun registerAccount(account: Account): Boolean
    fun getAccount(uID: String): Account?
}

open class FirebaseAccountDAOImpl(context: Context): FirebaseUserDAOImpl(context), FirebaseAccountDAO{
    override fun registerAccount(account: Account): Boolean {
        val task = fireStore
            .collection(Constants.CollectionAccounts)
            .document(account.uID)
            .set(account, SetOptions.merge())
        return if (task.isSuccessful){
            auth.signOut()
            Toast.makeText(context, "Account Registration Successful", Toast.LENGTH_SHORT).show()
            true
        }else{
            Log.e("Account Registration", task.exception!!.message.toString())
            false
        }
    }

    override fun getAccount(uID: String): Account? {
        val task = fireStore
            .collection(Constants.CollectionAccounts)
            .document(uID)
            .get()
        return if (task.isSuccessful){
            Log.i("Account Document Retrieved", task.result.toString())
            task.result.toObject(Account::class.java)!!
        }else{
            Log.e("Account Error", task.exception!!.message.toString())
            null
        }
    }

}