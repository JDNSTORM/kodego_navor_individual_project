package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactNumber(
    var areaCode: String = "",
    var contact: Long = 0
): Parcelable{
    fun telephone(): String{
        return if (contact != 0L) {
            "($areaCode) $contact"
        }else{
            ""
        }
    }
}