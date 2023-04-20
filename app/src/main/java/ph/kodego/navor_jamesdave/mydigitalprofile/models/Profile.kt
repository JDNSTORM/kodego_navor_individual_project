package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase_models.FirebaseProfile

/**
 * Data Flow
 *  Home = ProfileDatas
 *      -> ProfileActivity = ProfileData
 *          -> Fragments = Profile
 *
 */

data class Profile(
    var profession: String = ""
): Account(), Parcelable{
    var profileID: String = ""

    fun setProfile(profile: Profile){
        profession = profile.profession
        profileID = profile.profileID
        setAccount(profile)
    }

    override fun setParcel(parcel: Parcel) {
        super.setParcel(parcel)
        profession = parcel.readString()!!
        profileID = parcel.readString()!!
    }
    constructor(parcel: Parcel) : this() {
        this.setParcel(parcel)
    }

    constructor(profileID: String, profession: String = ""): this(profession){
        this.profileID = profileID
    }
    fun exportFirebaseProfile(): FirebaseProfile{
        return FirebaseProfile(uID, profileID, profession)
    }
    fun importFirebaseProfile(firebaseProfile: FirebaseProfile){
        uID = firebaseProfile.uid
        profileID = firebaseProfile.profileID
        profession = firebaseProfile.profession
    }
    constructor(firebaseProfile: FirebaseProfile): this(firebaseProfile.profession){
        uID = firebaseProfile.uid
        profileID = firebaseProfile.profileID
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(profession)
        parcel.writeString(profileID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}

@Parcelize
data class ProfessionalSummary(
    val profileID: String = ""
): Parcelable{
    var id: String = ""
    var profileSummary: String = ""

    fun setSummary(professionalSummary: ProfessionalSummary){
        id = professionalSummary.id
        profileSummary = professionalSummary.profileSummary
    }
}

data class ProfileCareer(
    val profile: Profile?,
    val careers: ArrayList<Career>
)

data class UserSkills(
    val profile: Profile,
    val skills: ArrayList<SkillMainCategory> = ArrayList()
)