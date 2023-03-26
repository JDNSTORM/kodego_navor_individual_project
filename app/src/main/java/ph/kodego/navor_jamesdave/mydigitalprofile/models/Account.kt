package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

open class Account (
    var uID: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var contactInformationID: String = "",
): Parcelable {
    var image: String = ""
    var fcmToken: String = ""

    @get:Exclude
    var contactInformation: ContactInformation? = null
//        private set

    /** Parcelable Constructor */
    constructor(parcel: Parcel) : this(){
        setParcel(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uID)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(contactInformationID)
        parcel.writeString(image)
        parcel.writeString(fcmToken)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Account> {
        override fun createFromParcel(parcel: Parcel): Account = Account(parcel)
        override fun newArray(size: Int): Array<Account?> = arrayOfNulls(size)
    }

    open fun setAccount(account: Account){
        uID = account.uID
        firstName = account.firstName
        lastName = account.lastName
        contactInformationID = account.contactInformationID
        image = account.image
        fcmToken = account.fcmToken
    }

    open fun setParcel(parcel: Parcel){
        uID = parcel.readString()!!
        firstName = parcel.readString()!!
        lastName = parcel.readString()!!
        contactInformationID = parcel.readString()!!
        image = parcel.readString()!!
        fcmToken = parcel.readString()!!
    }
    constructor(account: Account) : this(){
        setAccount(account)
    }
}