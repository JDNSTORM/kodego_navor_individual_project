package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
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
}

@Parcelize
data class EmailAddress( //TODO: Inherit ContactInformation?
    var id: String = "",
    var contactInformationID: String = "",
    var email: String = ""
): Parcelable

@Parcelize
data class Address(
    var id: String = "",
    val contactInformationID: String = "",
    var streetAddress: String = "",
    var subdivision: String = "",
    var cityOrMunicipality: String = "",
    var province: String = "",
    var zipCode: Int = 0,
    var country: String = ""
): Parcelable

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