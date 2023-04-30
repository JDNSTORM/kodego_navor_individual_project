package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import ph.kodego.navor_jamesdave.mydigitalprofile.R
//TODO: Try Parcelize
open class Account (
    var uID: String = "",
    var firstName: String = "",
    var lastName: String = "",
): Parcelable {
    var contactInformationID: String = ""
    var image: String = ""
    var fcmToken: String = ""

    @get:Exclude
    var contactInformation: ContactInformation? = null

    /** Parcelable Constructor */
    constructor(parcel: Parcel) : this(){
        this.setParcel(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uID)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(contactInformationID)
        parcel.writeString(image)
        parcel.writeString(fcmToken)
        parcel.writeParcelable(contactInformation, flags)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Account> {
        override fun createFromParcel(parcel: Parcel): Account = Account(parcel)
        override fun newArray(size: Int): Array<Account?> = arrayOfNulls(size)
    }

    fun setAccount(account: Account){
        uID = account.uID
        firstName = account.firstName
        lastName = account.lastName
        contactInformationID = account.contactInformationID
        image = account.image
        fcmToken = account.fcmToken
        contactInformation = account.contactInformation
    }

    open fun setParcel(parcel: Parcel){
        uID = parcel.readString()!!
        firstName = parcel.readString()!!
        lastName = parcel.readString()!!
        contactInformationID = parcel.readString()!!
        image = parcel.readString()!!
        fcmToken = parcel.readString()!!
        contactInformation = parcel.readParcelable(ContactInformation::class.java.classLoader)
    }
    constructor(account: Account) : this(){
        setAccount(account)
    }
    constructor(uID: String, firstName: String, lastName: String, contactInformationID: String): this(uID, firstName, lastName){
        this.contactInformationID = contactInformationID
    }

    fun fullName(): String = "$firstName $lastName"
}