package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

import com.google.firebase.firestore.DocumentId

data class Career(
    var companyName: String = "",
    var position: String = "",
    var employmentStart: String = "",
    var employmentEnd: String = "",
    var jobDescription: String = "",
    var emailAddress: String = "",
    var address: Address = Address(),
    var contactNumber: ContactNumber = ContactNumber(),
    var website: String = ""
){
    fun employmentPeriod(): String{
        return if (employmentStart.isNotEmpty() && employmentEnd.isNotEmpty()){
            "$employmentStart - $employmentEnd"
        }else{
            ""
        }
    }
}
