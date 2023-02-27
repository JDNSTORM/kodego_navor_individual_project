package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

class FireStore {
    private val fireStore = FirebaseFirestore.getInstance()

    fun registerAccount(account: Account): Boolean{
        var accountRegistered = false
        fireStore
            .collection(Constants.CollectionAccounts)
            .document(getCurrentUserID())
            .set(account, SetOptions.merge())
            .addOnSuccessListener {
                accountRegistered = true
            }
            .addOnFailureListener {
                Log.e("Account Registration", it.message.toString())

            }

        return accountRegistered //TODO: Return after fireStore Task Completes
    }

    private fun getCurrentUserID(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""
    }
}