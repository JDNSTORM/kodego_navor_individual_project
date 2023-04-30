package ph.kodego.navor_jamesdave.mydigitalprofile.extensions

import android.view.View
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Education

fun ViewholderEducationBinding.bind(education: Education) {
    dateEnrolled.text = education.dateEnrolled
    dateGraduated.text = education.dateGraduated
    schoolName.text = education.schoolName
    with(education.contactInformation!!) {
        schoolAddress.text = address!!.streetAddress
        val website = website!!.website
        if (website.isNotEmpty()) {
            schoolWebsite.text = website
            schoolWebsite.visibility = View.VISIBLE
        }
        val telephone = contactNumber!!.telephone()
        if (telephone.isNotEmpty()) {
            schoolTelephone.text = telephone
            schoolTelephone.visibility = View.VISIBLE
        }
    }
    degree.text = education.degree
    fieldOfStudy.text = education.fieldOfStudy
}