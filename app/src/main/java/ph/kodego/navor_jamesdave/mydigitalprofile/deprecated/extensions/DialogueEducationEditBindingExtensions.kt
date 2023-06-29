package ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.extensions

import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogEducationEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.models.Website
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.editInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface

fun DialogEducationEditBinding.clear() {
    dateEnrolled.text?.clear()
    dateGraduated.text?.clear()
    schoolName.text?.clear()
    schoolAddress.text?.clear()
    schoolWebsite.text?.clear()
    contactEdit.telContactNumber.text?.clear()
    degree.text?.clear()
    fieldOfStudy.text?.clear()

    editButtons.saveInterface()
}
fun DialogEducationEditBinding.bind(education: Education){
    dateEnrolled.setText(education.dateEnrolled)
    dateGraduated.setText(education.dateGraduated)
    schoolName.setText(education.schoolName)
    schoolAddress.setText(education.contactInformation!!.address!!.streetAddress)
    schoolWebsite.setText(education.contactInformation!!.website!!.website)
    contactEdit.telAreaCode.setText(education.contactInformation!!.contactNumber!!.areaCode)
    contactEdit.telContactNumber.setText(education.contactInformation!!.contactNumber!!.contact.toString())
    degree.setText(education.degree)
    fieldOfStudy.setText(education.fieldOfStudy)

    editButtons.editInterface()
}

fun DialogEducationEditBinding.storeContents(education: Education){
    education.dateEnrolled =  dateEnrolled.text.toString().trim()
    education.dateGraduated =  dateGraduated.text.toString().trim()
    education.schoolName =  schoolName.text.toString().trim()
    val contactInformation = if (education.contactInformation != null){
        education.contactInformation!!
    }else{
        ContactInformation()
    }
    if (contactInformation.address == null){
        contactInformation.address = Address()
    }
    if (contactInformation.website == null){
        contactInformation.website = Website()
    }
    if (contactInformation.contactNumber == null){
        contactInformation.contactNumber = ContactNumber()
    }
    contactInformation.address!!.streetAddress = schoolAddress.text.toString().trim()
    contactInformation.website!!.website = schoolWebsite.text.toString().trim()
    contactInformation.contactNumber!!.areaCode = contactEdit.telAreaCode.text.toString().trim()
    contactInformation.contactNumber!!.contact = contactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0

    education.degree = degree.text.toString().trim()
    education.fieldOfStudy = fieldOfStudy.text.toString().trim()

    education.contactInformation = contactInformation
}