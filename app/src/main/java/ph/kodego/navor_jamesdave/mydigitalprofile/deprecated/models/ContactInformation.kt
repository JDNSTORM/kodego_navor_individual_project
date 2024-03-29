package ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
open class ContactInformation(
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

    constructor(contactInformation: ContactInformation): this(contactInformation.contactInformationID){
        if (contactInformation.emailAddress != null){
            emailAddress = EmailAddress(contactInformation.emailAddress!!)
        }
        if (contactInformation.address != null){
            address = Address(contactInformation.address!!)
        }
        if (contactInformation.contactNumber != null){
            contactNumber = ContactNumber(contactInformation.contactNumber!!)
        }
        if (contactInformation.website != null){
            website = Website(contactInformation.website!!)
        }
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
data class EmailAddress(
    var contactInformationID: String = ""
): Parcelable {
    var id: String = ""
    var email: String = ""

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        id = parcel.readString()!!
        email = parcel.readString()!!
    }

    constructor(id: String, contactInformationID: String, email: String): this(contactInformationID){
        this.id = id
        this.email = email
    }

    constructor(emailAddress: EmailAddress): this(emailAddress.contactInformationID){
        id = emailAddress.id
        email = emailAddress.email
    }

    companion object : Parceler<EmailAddress> {

        override fun EmailAddress.write(parcel: Parcel, flags: Int) {
            parcel.writeString(contactInformationID)
            parcel.writeString(id)
            parcel.writeString(email)
        }

        override fun create(parcel: Parcel): EmailAddress {
            return EmailAddress(parcel)
        }
    }
}

@Parcelize
data class Address(
    var contactInformationID: String = ""
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

    fun completeAddress(): String{
        val address = StringBuilder()
        if (streetAddress.isNotEmpty()){
            address.append(streetAddress)
        }
        if (subdivision.isNotEmpty()){
            address.append(" $subdivision")
        }
        address.append(", ${localAddress()}")
        return address.trim(',').trim().toString()
    }
    fun localAddress(): String{
        val address = StringBuilder()
        if (cityOrMunicipality.isNotEmpty()){
            address.append(", $cityOrMunicipality")
        }
        if (province.isNotEmpty()){
            address.append(", $province")
        }
        if (country.isNotEmpty()){
            address.append(", $country")
        }

        return address.trim(',').trim().toString()
    }
}

@Parcelize
data class ContactNumber(
    var contactInformationID: String = "",
): Parcelable{
    var id: String = ""
    var areaCode: String = ""
    var contact: Long = 0

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        id = parcel.readString()!!
        areaCode = parcel.readString()!!
        contact = parcel.readLong()
    }
    constructor(contactNumber: ContactNumber) : this(contactNumber.contactInformationID) {
        id = contactNumber.id
        areaCode = contactNumber.areaCode
        contact = contactNumber.contact
    }

    companion object : Parceler<ContactNumber> {

        override fun ContactNumber.write(parcel: Parcel, flags: Int) {
            parcel.writeString(contactInformationID)
            parcel.writeString(id)
            parcel.writeString(areaCode)
            parcel.writeLong(contact)
        }

        override fun create(parcel: Parcel): ContactNumber {
            return ContactNumber(parcel)
        }
    }

    fun telephone(): String{
        return if (contact != 0L) {
            "($areaCode) $contact"
        }else{
            ""
        }
    }
}

@Parcelize
data class Website(
    var contactInformationID: String = ""
): Parcelable{
    var id: String = ""
    var website: String = ""

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        id = parcel.readString()!!
        website = parcel.readString()!!
    }

    companion object : Parceler<Website> {

        override fun Website.write(parcel: Parcel, flags: Int) {
            parcel.writeString(contactInformationID)
            parcel.writeString(id)
            parcel.writeString(website)
        }

        override fun create(parcel: Parcel): Website {
            return Website(parcel)
        }
    }
    constructor(website: Website):this(website.contactInformationID){
        id = website.id
        this.website = website.website
    }
}