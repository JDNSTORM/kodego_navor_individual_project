package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Career(
    val profileID: String = ""
): Parcelable{
    var id: String = ""
    var employmentStart: String = ""
    var employmentEnd: String = ""
    var position: String = ""
    var companyName: String = ""
//    var competencies: ArrayList<Competency> = ArrayList()
    var contactInformationID: String = ""

    @get:Exclude
    var contactInformation: ContactInformation? = null
}

@Parcelize
data class Competency(
    val careerID: String = ""
): Parcelable{
    var id: String = ""
    var competency: String = ""
}