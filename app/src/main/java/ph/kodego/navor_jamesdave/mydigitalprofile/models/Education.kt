package ph.kodego.navor_jamesdave.mydigitalprofile.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Education(val profileID: String = ""): Parcelable {
    var id: String = ""
    var contactInformationID: String = ""
    var dateEnrolled: String = ""
    var dateGraduated: String = ""
    var degree: String = ""
    var fieldOfStudy: String = ""
    var schoolName: String = ""

    @get:Exclude
    var contactInformation: ContactInformation? = null

    constructor(education: Education): this(education.profileID){
        id = education.id
        contactInformationID = education.contactInformationID
        dateEnrolled = education.dateEnrolled
        dateGraduated = education.dateGraduated
        degree = education.degree
        fieldOfStudy = education.fieldOfStudy
        schoolName = education.schoolName
        contactInformation = education.contactInformation
    }
}