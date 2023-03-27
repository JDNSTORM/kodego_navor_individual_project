package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile

interface FirebaseProfileDAO {
    suspend fun addProfile(profile: Profile): Boolean
    suspend fun getProfile(profileID: String): Profile
    suspend fun getProfiles(): ArrayList<Profile>
    suspend fun updateProfile(profile: Profile): Boolean
    suspend fun deleteProfile(profile: Profile): Boolean
}

class FirebaseProfileDAOImpl(context: Context): FirebaseAccountDAOImpl(context), FirebaseProfileDAO{
    private val collection = FirebaseCollections.Profile
    override suspend fun addProfile(profile: Profile): Boolean {
        val reference = fireStore
            .collection(FirebaseCollections.Accounts)
            .document(getCurrentUserID())
            .collection(collection)
            .document()
        profile.profileID = reference.id
        val task = reference.set(profile, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("Profile Creation", task.result.toString())
            true
        }else{
            Log.e("Profile Creation", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getProfile(profileID: String): Profile {
        TODO("Not yet implemented")
    }

    override suspend fun getProfiles(): ArrayList<Profile> {
        TODO("Not yet implemented")
    }

    override suspend fun updateProfile(profile: Profile): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProfile(profile: Profile): Boolean {
        TODO("Not yet implemented")
    }

}