package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile

interface FirebaseProfileDAO {
    suspend fun addProfile(profile: Profile): Boolean
    suspend fun getProfile(uID: String): Profile
    suspend fun getProfiles(): ArrayList<Profile>
    suspend fun updateProfile(profile: Profile, fields: HashMap<String, Any?>): Boolean
    suspend fun deleteProfile(profile: Profile): Boolean
}

open class FirebaseProfileDAOImpl(context: Context): FirebaseAccountDAOImpl(context), FirebaseProfileDAO{
    private val collection = FirebaseCollections.Profile
    private val collectionAccounts = FirebaseCollections.Accounts
    override suspend fun addProfile(profile: Profile): Boolean {
        val reference = fireStore
            .collection(collectionAccounts)
            .document(profile.uID)
            .collection(collection)
            .document(profile.uID)
        profile.profileID = reference.id
        val task = reference.set(profile.exportDaoProfile(), SetOptions.merge())
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
            .collection(collectionAccounts)
            .document(uID)
            .collection(collection)
            .document(uID)
            .get()
        task.await()
        return if (task.isSuccessful && task.result.data != null){
            Log.i("Profile", task.result.toString())
            task.result.toObject(Profile::class.java)!!
        }else{
            Log.e("Get Profile", task.exception?.message.toString())
            val profile = Profile()
            profile.uID = uID
            addProfile(profile)
            profile
        }
    }

    override suspend fun getProfiles(): ArrayList<Profile> {
        val profiles: ArrayList<Profile> = ArrayList()
        val task = fireStore.collectionGroup(collection).get()
        task.await()
        if (task.isSuccessful && task.result.documents.isNotEmpty()){
            Log.i("Profiles", task.result.documents.toString())
            task.result.documents.forEach { snapshot ->
                Log.i("Profile Data", snapshot.data.toString())
                val profile = snapshot.toObject(Profile::class.java)!!
                profile.setAccount(getAccount(profile.uID)!!)
                Log.d("Profile", profile.toString())
                profiles.add(profile)
            }
        }else{
            Log.e("Get Profiles", task.exception?.message.toString())
        }
        return profiles
    }

    override suspend fun updateProfile(profile: Profile, fields: HashMap<String, Any?>): Boolean {
        val task = fireStore
            .collection(collectionAccounts)
            .document(profile.uID)
            .collection(collection)
            .document(profile.profileID)
            .update(fields)
        task.await()
        return task.isSuccessful
    }

    override suspend fun deleteProfile(profile: Profile): Boolean {
//        TODO("Not yet implemented")
        val task = fireStore
            .collection(collectionAccounts)
            .document(profile.uID)
            .collection(collection)
            .document(profile.profileID)
            .delete()
        task.await()
        return task.isSuccessful
    }

}