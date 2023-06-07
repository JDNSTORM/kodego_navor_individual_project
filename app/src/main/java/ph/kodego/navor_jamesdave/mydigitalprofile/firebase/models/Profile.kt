package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Profile(
    @DocumentId
    val profileID: String = "",
    override val uID: String = "",
    var profession: String = ""
): Account(uID), Parcelable {

}