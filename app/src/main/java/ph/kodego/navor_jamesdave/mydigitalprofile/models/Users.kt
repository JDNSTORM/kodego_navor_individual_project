package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

/**
 * Data Flow
 *  Home = ProfileDatas
 *      -> ProfileActivity = ProfileData
 *          -> Fragments = Profile
 *
 */

open class User( //TODO: Use Parcelable?
    var profilePicture: Int = R.drawable.placeholder,
): Account(), Parcelable{
    var userID: Long = 0

    fun setUser(user: User){
        userID = user.userID
        profilePicture = user.profilePicture

        setAccount(user)
    }
    constructor(userID: Long = 0, profilePicture: Int = R.drawable.placeholder): this(profilePicture){
        this.userID = userID
    }

    constructor(parcel: Parcel) : this(parcel.readInt()) {
        userID = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) { //TODO: Extend for Inherited Data
        super.writeToParcel(parcel, flags)
        parcel.writeInt(profilePicture)
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

data class Profile( //TODO: ProfileData is used for Data instead, keep track
    var profession: String = ""
): User(), Parcelable{
    var profileID: Long = 0

    constructor(parcel: Parcel) : this() {
        uID = parcel.readString()!!
        firstName = parcel.readString()!!
        lastName = parcel.readString()!!
        contactInformationID = parcel.readString()!!
        profilePicture = parcel.readInt()
        userID = parcel.readLong()
//        Log.d("Parcel", parcel.readString()?:"NULL")
//        Log.d("Parcel", parcel.readString()?:"NULL")
//        Log.d("Parcel", parcel.readString()?:"NULL")
        profession = parcel.readString()!!
        profileID = parcel.readLong()

        //User
//        profilePicture = parcel.readInt()
//        Log.d("Parcel Picture", profilePicture.toString())
//        userID = parcel.readLong()

        //Account
//        uID = parcel.readString()!!
//        firstName = parcel.readString()!!
//        lastName = parcel.readString()!!
//        contactInformationID = parcel.readString()!!
    }

    constructor(profileID: Long, profession: String = ""): this(profession){
        this.profileID = profileID
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) { //TODO: Extend for Inherited Data
        super.writeToParcel(parcel, flags)
        parcel.writeString(profession)
        parcel.writeLong(profileID)

        //User
//        parcel.writeInt(profilePicture)
//        Log.d("Profile Picture", profilePicture.toString())
//        parcel.writeLong(userID)

        //Account
//        parcel.writeString(uID)
//        parcel.writeString(firstName)
//        parcel.writeString(lastName)
//        parcel.writeString(contactInformationID)
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
    val profile: Profile,
    val careers: ArrayList<Career>
)

data class UserSkills(
    val profile: Profile,
    val skills: ArrayList<SkillMainCategory> = ArrayList()
)

data class ContactInformation( //TODO: Many-to-One - User, Profile, Career, School
    var id: String = ""
): Parcelable{
    @get:Exclude
    var emailAddress: EmailAddress? = null
    @get:Exclude
    var address: Address? = null
    @get:Exclude
    var contactNumber: ContactNumber? = null
    @get:Exclude
    var website: Website? = null

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        emailAddress = parcel.readParcelable(EmailAddress::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeParcelable(emailAddress, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactInformation> {
        override fun createFromParcel(parcel: Parcel): ContactInformation {
            return ContactInformation(parcel)
        }

        override fun newArray(size: Int): Array<ContactInformation?> {
            return arrayOfNulls(size)
        }
    }


}

data class EmailAddress( //TODO: Insert into User?
    var id: String = "",
    val contactInformationID: String,
    var email: String
): Parcelable{
    constructor(): this("", "", "")
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(contactInformationID)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmailAddress> {
        override fun createFromParcel(parcel: Parcel): EmailAddress {
            return EmailAddress(parcel)
        }

        override fun newArray(size: Int): Array<EmailAddress?> {
            return arrayOfNulls(size)
        }
    }

}

data class Address(
    var id: Long = 0,
    val contactInformationID: Long,
    var streetAddress: String = "",
    var subdivision: String = "",
    var cityOrMunicipality: String,
    var province: String,
    var zipCode: Int,
    var country: String
)

data class ContactNumber(
    var id: Long = 0,
    val contactInformationID: Int,
    var areaCode: Int,
    var contact: Int
)

data class Website(
    var id: Long = 0,
    val contactInformationID: Long,
    var domain: String
)