package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources

import com.google.firebase.firestore.DocumentSnapshot
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.FirestorePagingSource
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

class ProfilePagingSource(
    getList: suspend (DocumentSnapshot?, Int) -> List<DocumentSnapshot>,
    private val getAccount: suspend (String) -> Account,
    private val query: String = ""
): FirestorePagingSource<Profile>(getList) {
    override suspend fun DocumentSnapshot.toObject(): Profile {
        val profile = toObject(Profile::class.java)!!
        profile.setAccount(getAccount(profile.refUID))
        return profile
    }

    override suspend fun List<DocumentSnapshot>.toModels(): List<Profile> {
        val profiles = map { it.toObject() }
        return if (query.isNotEmpty()) {
            profiles.filter { profile ->
                profile.displayName().contains(query, true)
                    || profile.profession.contains(query, true)
            }
        } else profiles
    }
}