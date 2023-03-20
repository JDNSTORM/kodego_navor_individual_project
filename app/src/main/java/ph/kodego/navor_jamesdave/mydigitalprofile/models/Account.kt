package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude

data class Account (
    val uID: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var image: String = "",
    var contactInformationID: String? = null,
    val fcmToken: String = ""
): Parcelable {
    @get:Exclude
    var contactInformation: ContactInformation? = null
//        private set

    fun getContactInformation(){

    }

    /** Parcelable Constructor */
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uID)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(image)
        parcel.writeValue(contactInformationID)
        parcel.writeString(fcmToken)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Account> {
        override fun createFromParcel(parcel: Parcel): Account = Account(parcel)
        override fun newArray(size: Int): Array<Account?> = arrayOfNulls(size)
    }
}