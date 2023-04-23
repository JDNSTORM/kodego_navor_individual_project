package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import android.view.View
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueEducationEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Website

fun DialogueEducationEditBinding.clear() {
    dateEnrolled.text?.clear()
    dateGraduated.text?.clear()
    schoolName.text?.clear()
    schoolAddress.text?.clear()
    schoolWebsite.text?.clear()
    contactEdit.telContactNumber.text?.clear()
    degree.text?.clear()
    fieldOfStudy.text?.clear()
    with(editButtons){
        btnSave.visibility = View.VISIBLE
        btnUpdate.visibility = View.GONE
        btnDelete.visibility = View.GONE
    }
}
fun DialogueEducationEditBinding.bind(education: Education){
    dateEnrolled.setText(education.dateEnrolled)
    dateGraduated.setText(education.dateGraduated)
    schoolName.setText(education.schoolName)
    schoolAddress.setText(education.contactInformation!!.address!!.streetAddress)
    schoolWebsite.setText(education.contactInformation!!.website!!.website)
    contactEdit.telAreaCode.setText(education.contactInformation!!.contactNumber!!.areaCode)
    contactEdit.telContactNumber.setText(education.contactInformation!!.contactNumber!!.contact.toString())
    degree.setText(education.degree)
    fieldOfStudy.setText(education.fieldOfStudy)
    with(editButtons){
        btnSave.visibility = View.GONE
        btnUpdate.visibility = View.VISIBLE
        btnDelete.visibility = View.VISIBLE
    }
}

fun DialogueEducationEditBinding.getContents(education: Education){
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