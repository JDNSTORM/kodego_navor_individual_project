package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
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

    companion object : Parceler<ContactInformation> {

        override fun ContactInformation.write(parcel: Parcel, flags: Int) {
            parcel.writeString(contactInformationID)
            parcel.writeParcelable(emailAddress, flags)
            parcel.writeParcelable(address, flags)
            parcel.writeParcelable(contactNumber, flags)
            parcel.writeParcelable(website, flags)
        }

        override fun create(parcel: Parcel): ContactInformation {
            return ContactInformation(parcel)
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

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        id = parcel.readString()!!
        streetAddress = parcel.readString()!!
        subdivision = parcel.readString()!!
        cityOrMunicipality = parcel.readString()!!
        province = parcel.readString()!!
        zipCode = parcel.readInt()
        country = parcel.readString()!!
    }

    constructor(address: Address): this(address.contactInformationID){
        id = address.id
        streetAddress = address.streetAddress
        subdivision = address.subdivision
        cityOrMunicipality = address.cityOrMunicipality
        province = address.cityOrMunicipality
        zipCode = address.zipCode
        country = address.country
    }

    companion object : Parceler<Address> {

        override fun Address.write(parcel: Parcel, flags: Int) {
            parcel.writeString(contactInformationID)
            parcel.writeString(id)
            parcel.writeString(streetAddress)
            parcel.writeString(subdivision)
            parcel.writeString(cityOrMunicipality)
            parcel.writeString(province)
            parcel.writeInt(zipCode)
            parcel.writeString(country)
        }

        override fun create(parcel: Parcel): Address {
            return Address(parcel)
        }
    }
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