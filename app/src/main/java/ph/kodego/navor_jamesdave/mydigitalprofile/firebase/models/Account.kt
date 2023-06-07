package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    @DocumentId
    val uID: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = ""
): Parcelable {
    fun displayName() = "$firstName $lastName".trim()

    constructor(firstName: String, lastName: String, email: String): this("", firstName, lastName, email)
}