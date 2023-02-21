package ph.kodego.navor_jamesdave.mydigitalprofile.models

import ph.kodego.navor_jamesdave.mydigitalprofile.R

data class UserData( //TODO: For Data Purposes
    var id: Long = 0,
    val accountID: Long,
    var profilePicture: Int = R.drawable.placeholder,
    var firstName: String,
    var lastName: String,
    val contactInformation: ContactInformation
): java.io.Serializable

data class ProfileData(
    var id: Long = 0,
    val user: UserData,
    var profession: String,
    val profileSummary: String
): java.io.Serializable