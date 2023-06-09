package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import com.google.firebase.firestore.DocumentId

data class Profile(
    @DocumentId
    val profileID: String = "",
    override val uID: String = "",
    var profession: String = "",
    var isPublic: Boolean = false,
    var profileSummary: String = "",
    val careers: ArrayList<Career> = ArrayList(),
    val skills: ArrayList<SkillsMain> = ArrayList(),
    val educations: ArrayList<Education> = ArrayList()
): Account(uID) {

}