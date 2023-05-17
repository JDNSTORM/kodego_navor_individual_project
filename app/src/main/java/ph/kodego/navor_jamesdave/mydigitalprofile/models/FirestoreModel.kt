package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class FirestoreModel: Parcelable {
    var documentID: String = ""
}