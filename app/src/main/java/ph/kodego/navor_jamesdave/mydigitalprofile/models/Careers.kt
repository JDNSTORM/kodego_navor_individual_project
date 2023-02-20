package ph.kodego.navor_jamesdave.mydigitalprofile.models

data class Career(
    var id: Int = 0,
    val userID: Int,
    var employmentStart: String,
    var employmentEnd: String,
    var position: String,
    var companyName: String,
    var competencies: ArrayList<Competency>,
    val contactInformationID: Int,
)

data class Competency(
    var id: Int = 0,
    val careerID: Int,
    var competency: String
)