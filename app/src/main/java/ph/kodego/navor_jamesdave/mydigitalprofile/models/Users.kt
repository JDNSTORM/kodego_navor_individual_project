package ph.kodego.navor_jamesdave.mydigitalprofile.models

import ph.kodego.navor_jamesdave.mydigitalprofile.R

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

data class ContactInformation( //TODO: Many-to-One - User, Career, School
    var id: String = "",
    var emailAddress: EmailAddress?,
    var address: Address? = null,
    var contactNumber: ContactNumber? = null,
    var website: Website? = null,
): java.io.Serializable

data class EmailAddress( //TODO: Insert into User?
    var id: Long = 0,
    val contactInformationID: Long,
    var username: String,
    var domain: String
): java.io.Serializable

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