package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import android.view.View
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueEducationEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Education

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