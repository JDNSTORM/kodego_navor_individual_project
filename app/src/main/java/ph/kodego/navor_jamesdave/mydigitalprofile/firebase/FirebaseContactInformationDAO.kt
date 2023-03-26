package ph.kodego.navor_jamesdave.mydigitalprofile.firebase

import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.EmailAddress

interface FirebaseContactInformationDAO {
    fun registerContactInformation(contactInformation: ContactInformation)
    fun getContactInformation(contactInformationID: String): ContactInformation
    fun registerEmail(emailAddress: EmailAddress)
    fun getEmail(contactInformation: ContactInformation): EmailAddress?
}

class FirebaseContactInformationDAOImpl(){

}