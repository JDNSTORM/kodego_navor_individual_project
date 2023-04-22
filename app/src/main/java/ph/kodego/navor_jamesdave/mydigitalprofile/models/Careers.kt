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
    var jobDescription: String = ""

    @get:Exclude
    var contactInformation: ContactInformation? = null

    constructor(career: Career): this(career.profileID){
        id = career.id
        employmentStart = career.employmentStart
        employmentEnd = career.employmentEnd
        position = career.position
        companyName = career.companyName
        contactInformationID = career.contactInformationID
        jobDescription = career.jobDescription
        contactInformation = career.contactInformation
    }

    fun employmentPeriod(): String{
        return if (employmentStart.isNotEmpty() && employmentEnd.isNotEmpty()){
            "$employmentStart - $employmentEnd"
        }else{
            ""
        }
    }
}

@Parcelize
data class Competency(
    val careerID: String = ""
): Parcelable{
    var id: String = ""
    var competency: String = ""
}