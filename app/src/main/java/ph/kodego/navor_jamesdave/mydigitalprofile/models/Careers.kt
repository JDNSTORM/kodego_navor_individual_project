package ph.kodego.navor_jamesdave.mydigitalprofile.models

data class Career(
    var id: String = "",
    val profileID: Int,
    var employmentStart: String,
    var employmentEnd: String,
    var position: String,
    var companyName: String,
    var competencies: ArrayList<Competency>,
    val contactInformationID: String,
)

data class Competency(
    var id: String = "",
    val careerID: String,
    var competency: String
)