package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

data class Profile(
    @DocumentId
    val profileID: String = "",
    @PropertyName("uid")
    val refUID: String = "",
    var profession: String = "",
    var isPublic: Boolean = false,
    var profileSummary: String = "",
    val careers: ArrayList<Career> = ArrayList(),
    val skills: ArrayList<SkillsMain> = ArrayList(),
    val educations: ArrayList<Education> = ArrayList()
): Account(refUID) {
    constructor(uID: String, profession: String, isPublic: Boolean): this("", uID, profession, isPublic)

    fun toFirestore(): FirestoreProfile{
        return  FirestoreProfile(
            profileID,
            refUID,
            profession,
            isPublic,
            profileSummary,
            careers,
            skills,
            educations
        )
    }
}
