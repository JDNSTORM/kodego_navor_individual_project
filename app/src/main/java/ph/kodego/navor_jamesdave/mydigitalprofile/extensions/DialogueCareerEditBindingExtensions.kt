package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogCareerEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career

fun DialogCareerEditBinding.clear() {
    dateEmployed.text?.clear()
    employmentEnd.text?.clear()
    position.text?.clear()
    company.text?.clear()
    companyAddress.text?.clear()
    companyWebsite.text?.clear()
    layoutContactEdit.telContactNumber.text?.clear()
    jobDescription.text?.clear()

    editButtons.saveInterface()
}
fun DialogCareerEditBinding.bind(career: Career){
    dateEmployed.setText(career.employmentStart)
    employmentEnd.setText(career.employmentEnd)
    position.setText(career.position)
    position.setText(career.position)
    company.setText(career.companyName)
    val address = career.contactInformation?.address
    if (address != null) {
        companyAddress.setText(address.streetAddress)
    }
    val website = career.contactInformation?.website
    if (website != null) {
        companyWebsite.setText(website.website)
    }
    val telephone = career.contactInformation?.contactNumber
    if (telephone != null) {
        layoutContactEdit.telAreaCode.setText(telephone.areaCode)
        layoutContactEdit.telContactNumber.setText(telephone.contact.toString())
    }
    jobDescription.setText(career.jobDescription)

    editButtons.editInterface()
}