package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    var streetAddress: String = "",
    var subdivision: String = "",
    var cityOrMunicipality: String = "",
    var province: String = "",
    var zipCode: Int = 0,
    var country: String = ""
): Parcelable{
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
