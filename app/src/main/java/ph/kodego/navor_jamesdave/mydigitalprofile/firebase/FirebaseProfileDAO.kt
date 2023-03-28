package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile

interface FirebaseProfileDAO {
    suspend fun addProfile(profile: Profile): Boolean
    suspend fun getProfile(uID: String): Profile
    suspend fun getProfiles(): ArrayList<Profile>
    suspend fun updateProfile(profile: Profile): Boolean
    suspend fun deleteProfile(profile: Profile): Boolean
}

class FirebaseProfileDAOImpl(context: Context): FirebaseAccountDAOImpl(context), FirebaseProfileDAO{
    private val collection = FirebaseCollections.Profile
    override suspend fun addProfile(profile: Profile): Boolean { //TODO: uID
        val reference = fireStore
            .collection(FirebaseCollections.Accounts)
            .document(profile.uID)
            .collection(collection)
            .document(profile.uID)
        profile.profileID = reference.id
        val task = reference.set(profile, SetOptions.merge())
        task.await()
        return if (task.isSuccessful){
            Log.i("Profile Creation", "Successful")
            true
        }else{
            Log.e("Profile Creation", task.exception!!.message.toString())
            false
        }
    }

    override suspend fun getProfile(uID: String): Profile {
        val task = fireStore
//            .collection(FirebaseCollections.Accounts) //TODO: Cannot find profile without this
//            .document(uID)
            .collection(collection)
            .document(uID)
            .get()
        task.await()
        return if (task.isSuccessful && task.result.data != null){
            Log.i("Profile", task.result.toString())
            val profile = task.result.toObject(Profile::class.java)!!
            profile
        }else{
            Log.e("Get Profile", task.exception?.message.toString())
            val profile = Profile()
            profile.uID = uID
            addProfile(profile)
            return profile
        }
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