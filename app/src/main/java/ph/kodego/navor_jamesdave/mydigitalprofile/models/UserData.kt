package ph.kodego.navor_jamesdave.mydigitalprofile.models

import ph.kodego.navor_jamesdave.mydigitalprofile.R
//TODO: For Data Purposes
data class UserData(
    var id: Long = 0,
    val accountID: Long,
    var profilePicture: Int = R.drawable.placeholder,
    var firstName: String,
    var lastName: String,
    val contactInformation: ContactInformation
): java.io.Serializable

data class ProfileData(
    val user: User,
    val profile: Profile
): java.io.Serializable