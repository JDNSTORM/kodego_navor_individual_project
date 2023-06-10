package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import com.google.firebase.firestore.DocumentId

open class Account(
    @DocumentId
    val uid: String = ""
) {
    var firstName: String = ""
    var lastName: String = ""
    var image: String = ""
    var emailAddress: String = ""
    var address: Address = Address()
    var contactNumber: ContactNumber = ContactNumber()
    var website: String = ""
    var fcmTokem: String = ""

    fun displayName() = "$firstName $lastName".trim()

    fun setAccount(account: Account){
        firstName = account.firstName
        lastName = account.lastName
        emailAddress = account.emailAddress
        address = account.address.copy()
        contactNumber = account.contactNumber.copy()
        website = account.website
    }

    constructor(firstName: String, lastName: String, email: String): this(){
        this.firstName = firstName
        this.lastName = lastName
        this.emailAddress = email
    }
    constructor(account: Account, firstName: String = "", lastName: String = ""): this(account.uid){
        this.firstName = firstName
        this.lastName = lastName
        setAccount(account)
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