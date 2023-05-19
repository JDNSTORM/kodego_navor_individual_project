package ph.kodego.navor_jamesdave.mydigitalprofile.firestore

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCollections
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile

interface ProfileDAO {
    suspend fun addProfile(profile: Profile): Boolean
    suspend fun getProfile(profileID: String): Profile?
    suspend fun getProfiles(): ArrayList<Profile>
}

open class ProfileDAOImpl(): AccountDAOImpl(), ProfileDAO {
    override val collection: String
        get() = FirebaseCollections.Profile

    override fun getCollectionReference(): CollectionReference {
        return super.getCollectionReference().document().collection(collection)
    }

    override suspend fun addProfile(profile: Profile): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getProfile(profileID: String): Profile? {
        TODO("Not yet implemented")
    }

    override suspend fun getProfiles(): ArrayList<Profile> {
        val profiles: ArrayList<Profile> = ArrayList()
        val documents = getAllDocuments()
        documents?.forEach { document ->
            Log.i("Profile Data", document.data.toString())
            val profile = document.toObject(Profile::class.java)!!
            getAccount(profile.uID)
//            profile.setAccount(getAccount(profile.uID)!!)
            profiles.add(profile)
        }
        return profiles
    }
}