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
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uID)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(contactInformationID)
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
    }
    constructor(account: Account) : this(){
        setAccount(account)
    }
}