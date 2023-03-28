package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import ph.kodego.navor_jamesdave.mydigitalprofile.R

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


    override fun setParcel(parcel: Parcel) {
        super.setParcel(parcel)
        profession = parcel.readString()!!
        uID = parcel.readString()!!
        profileID = parcel.readString()!!
    }
    constructor(parcel: Parcel) : this() {
        this.setParcel(parcel)
    }

    constructor(profileID: String, profession: String = ""): this(profession){
        this.profileID = profileID
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(profession)
        parcel.writeString(uID)
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

data class ProfileSummary(
    var id: Long = 0,
    val profileID: Long,
    var profileSummary: String
)

data class ProfileCareer(
    val profile: Profile?,
    val careers: ArrayList<Career>
)

data class UserSkills(
    val profile: Profile,
    val skills: ArrayList<SkillMainCategory> = ArrayList()
)