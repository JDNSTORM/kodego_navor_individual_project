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

data class User( //TODO: Use Parcelable?
    var id: Long = 0,
    val accountID: String,
    var profilePicture: Int = R.drawable.placeholder,
    var firstName: String,
    var lastName: String,
    val contactInformationID: String
): java.io.Serializable

data class Profile( //TODO: ProfileData is used for Data instead, keep track
    var id: Long = 0,
    val userID: Long,
    var profession: String
): java.io.Serializable

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

    constructor(parcel: Parcel) : this(parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
    }

    override fun describeContents(): Int = 0

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