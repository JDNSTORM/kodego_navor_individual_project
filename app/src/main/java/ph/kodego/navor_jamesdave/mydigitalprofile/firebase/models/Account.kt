package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
open class Account(
    @DocumentId
    open val uID: String = ""
): Parcelable {
    var firstName: String = ""
    var lastName: String = ""
    var image: String = ""
    var emailAddress: String = ""
    var address: Address? = null
    var contactNumber: ContactNumber? = null
    var website: String = ""
    var fcmTokem: String = ""

    fun displayName() = "$firstName $lastName".trim()

    constructor(firstName: String, lastName: String, email: String): this(){
        this.firstName = firstName
        this.lastName = lastName
        this.emailAddress = email
    }
    constructor(account: Account, firstName: String, lastName: String): this(account.uID){
        this.firstName = firstName
        this.lastName = lastName
        this.emailAddress = account.emailAddress
    }

    companion object{
        const val KEY_FIRST_NAME = "firstName"
        const val KEY_LAST_NAME = "lastName"
        const val KEY_IMAGE = "image"
        const val KEY_EMAIL_ADDRESS = "emailAddress"
        const val KEY_ADDRESS = "address"
        const val KEY_CONTACT_NUMBER = "contactNumber"
        const val KEY_WEBSITE = "website"
    }
}