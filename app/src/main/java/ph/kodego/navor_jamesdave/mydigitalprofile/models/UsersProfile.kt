package ph.kodego.navor_jamesdave.mydigitalprofile.models

import ph.kodego.navor_jamesdave.mydigitalprofile.R

data class UsersProfile( //TODO: Use Parcelable?
    var id: Int = 0,
    var profilePicture: Int = R.drawable.placeholder,
    var firstName: String,
    var lastName: String,
    var profession: String
): java.io.Serializable