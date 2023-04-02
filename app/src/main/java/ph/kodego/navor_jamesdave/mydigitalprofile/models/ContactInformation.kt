package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

open class ContactInformation( //TODO: Many-to-One - User, Profile, Career, School
    var contactInformationID: String = ""
): Parcelable {
    @get:Exclude
    var emailAddress: EmailAddress? = null
    @get:Exclude
    var address: Address? = null
    @get:Exclude
    var contactNumber: ContactNumber? = null
    @get:Exclude
    var website: Website? = null

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        emailAddress = parcel.readParcelable(EmailAddress::class.java.classLoader)
        address = parcel.readParcelable(Address::class.java.classLoader)
        contactNumber = parcel.readParcelable(ContactNumber::class.java.classLoader)
        website = parcel.readParcelable(Website::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(contactInformationID)
        parcel.writeParcelable(emailAddress, flags)
        parcel.writeParcelable(address, flags)
        parcel.writeParcelable(contactNumber, flags)
        parcel.writeParcelable(website, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactInformation> {
        override fun createFromParcel(parcel: Parcel): ContactInformation {
            return ContactInformation(parcel)
        }

        override fun newArray(size: Int): Array<ContactInformation?> {
            return arrayOfNulls(size)
        }
    }
}

@Parcelize
data class EmailAddress( //TODO: Inherit ContactInformation?
    var id: String = "",
    var contactInformationID: String = "",
    var email: String = ""
): Parcelable

@Parcelize
data class Address(
    val contactInformationID: String = ""
): Parcelable {
    var id: String = ""
    var streetAddress: String = ""
    var subdivision: String = ""
    var cityOrMunicipality: String = ""
    var province: String = ""
    var zipCode: Int = 0
    var country: String = ""
}

@Parcelize
data class ContactNumber(
    var id: String = "",
    val contactInformationID: String = "",
    var areaCode: String = "",
    var contact: Int = 0
): Parcelable

@Parcelize
data class Website(
    var id: String = "",
    val contactInformationID: String = "",
    var website: String = ""
): Parcelable