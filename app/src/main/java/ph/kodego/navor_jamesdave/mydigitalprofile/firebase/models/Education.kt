package ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models

data class Education(
    var schoolName: String = "",
    var degree: String = "",
    var fieldOfStudy: String = "",
    var dateEnrolled: String = "",
    var dateGraduated: String = "",
    var emailAddress: String = "",
    var address: Address = Address(),
    var contactNumber: ContactNumber = ContactNumber(),
    var website: String = ""
)
