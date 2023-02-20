package ph.kodego.navor_jamesdave.mydigitalprofile.models

import ph.kodego.navor_jamesdave.mydigitalprofile.R

data class User( //TODO: Use Parcelable?
    var id: Int = 0,
    val accountID: Int,
    var profilePicture: Int = R.drawable.placeholder,
    var firstName: String,
    var lastName: String,
    val email: String, //TODO:
    val contactInformationID: Int
): java.io.Serializable

data class Profile(
    var id: Int = 0,
    val userID: Int,
    var profession: String,
    val profileSummary: String
): java.io.Serializable
data class UserData( //TODO: For Data Purposes
    var id: Int = 0,
    val accountID: Int,
    var profilePicture: Int = R.drawable.placeholder,
    var firstName: String,
    var lastName: String,
    val email: String, //TODO:
    val contactInformation: ContactInformationData
)
data class ProfileData(
    var id: Int = 0,
    val user: User,
    var profession: String,
    val profileSummary: String
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
    var id: Int = 0,
    var emailAddress: EmailAddress?,
    var address: Address? = null,
    var contactNumber: ContactNumber? = null,
    var website: Website? = null,
)

data class EmailAddress( //TODO: Insert into User?
    var id: Int = 0,
    val contactInformationID: Int,
    var username: String,
    var domain: String
)

data class Address(
    var id: Int = 0,
    val contactInformationID: Int,
    var streetAddress: String = "",
    var subdivision: String = "",
    var cityOrMunicipality: String,
    var province: String,
    var zipCode: Int,
    var country: String
)

data class ContactNumber(
    var id: Int = 0,
    val contactInformationID: Int,
    var areaCode: Int,
    var contact: Int
)

data class Website(
    var id: Int = 0,
    val contactInformationID: Int,
    var domain: String
)