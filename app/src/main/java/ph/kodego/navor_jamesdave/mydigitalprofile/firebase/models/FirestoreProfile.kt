package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class FirestoreProfile(
    @DocumentId
    val profileID: String = "",
    val uid: String = "",
    val profession: String = "",
    val isPublic: Boolean = false,
    val profileSummary: String = "",
    val careers: ArrayList<Career> = ArrayList(),
    val skills: ArrayList<SkillsMain> = ArrayList(),
    val educations: ArrayList<Education> = ArrayList()
)