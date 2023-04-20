package ph.kodego.navor_jamesdave.mydigitalprofile.firebase_models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseProfile(
    val uid: String = "",
    val profileID: String = "",
    var profession: String = ""
): Parcelable