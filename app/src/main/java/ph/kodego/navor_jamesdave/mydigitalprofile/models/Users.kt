package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import ph.kodego.navor_jamesdave.mydigitalprofile.R

/**
 * Data Flow
 *  Home = ProfileDatas
 *      -> ProfileActivity = ProfileData
 *          -> Fragments = Profile
 *
 */

open class User(): Account(), Parcelable{ //TODO: Not Used
    var userID: Long = 0

    fun setUser(user: User){
        userID = user.userID

        setAccount(user)
    }

    override fun setParcel(parcel: Parcel) { //TODO: Update this for updates with data
        super.setParcel(parcel)
        userID = parcel.readLong()
    }
    constructor(userID: Long = 0, profilePicture: Int = R.drawable.placeholder): this(){
        this.userID = userID
    }

    constructor(parcel: Parcel) : this() {
        setParcel(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) { //TODO: Extend for Inherited Data
        super.writeToParcel(parcel, flags)
        parcel.writeLong(userID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}

data class Profile(
    var profession: String = ""
): Account(), Parcelable{
    var profileID: String = ""


    override fun setParcel(parcel: Parcel) {
        super.setParcel(parcel)
        profession = parcel.readString()!!
        profileID = parcel.readString()!!
    }
    constructor(parcel: Parcel) : this() {
        setParcel(parcel)
    }

    constructor(profileID: String, profession: String = ""): this(profession){
        this.profileID = profileID
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

data class ProfileSummary(
    var id: Long = 0,
    val profileID: Long,
    var profileSummary: String
)

data class ProfileCareer(
    val profile: Profile?,
    val careers: ArrayList<Career>
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Profile::class.java.classLoader),
        TODO("careers")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(profile, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProfileCareer> {
        override fun createFromParcel(parcel: Parcel): ProfileCareer {
            return ProfileCareer(parcel)
        }

        override fun newArray(size: Int): Array<ProfileCareer?> {
            return arrayOfNulls(size)
        }
    }
}

data class UserSkills(
    val profile: Profile,
    val skills: ArrayList<SkillMainCategory> = ArrayList()
)