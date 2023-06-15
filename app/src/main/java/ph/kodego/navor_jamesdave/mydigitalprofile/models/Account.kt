package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.ContactNumber

//TODO: Try Parcelize
@Entity(tableName = "accounts-table")
open class Account (
    @PrimaryKey(false)
    var uID: String = "",
    var firstName: String = "",
    var lastName: String = "",
): Parcelable {
    var contactInformationID: String = ""
    var image: String = ""
    var fcmToken: String = ""

    @Ignore
    @get:Exclude
    var contactInformation: ContactInformation? = null

    fun migrateAccount(): ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account{
        val account =  ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account(
            uID,
            firstName,
            lastName,
        )
        contactInformation?.apply {
            emailAddress?.let {
                account.emailAddress = it.email
            }
            address?.let {
                account.address = Address(
                    it.streetAddress,
                    it.subdivision,
                    it.cityOrMunicipality,
                    it.province,
                    it.zipCode,
                    it.country
                )
            }
            contactNumber?.let {
                account.contactNumber = ContactNumber(
                    it.areaCode,
                    it.contact
                )
            }
            website?.let {
                account.website = it.website
            }
        }
        return account
    }

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