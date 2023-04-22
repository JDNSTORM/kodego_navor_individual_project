package ph.kodego.navor_jamesdave.mydigitalprofile.utils

import android.view.View
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ViewholderCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career

fun ViewholderCareerBinding.bind(career: Career) {
    position.text = career.position
    employmentPeriod.text = career.employmentStart + " - " + career.employmentEnd
    companyName.text = career.companyName
    val address = career.contactInformation?.address?.completeAddress()
    if (!address.isNullOrEmpty()) {
        companyAddress.text = address
        companyAddress.visibility = View.VISIBLE
    }
    val website = career.contactInformation?.website?.website
    if (!website.isNullOrEmpty()) {
        companyWebsite.text = website
        companyWebsite.visibility = View.VISIBLE
    }
    val contactNumber = career.contactInformation?.contactNumber
    if (contactNumber != null && contactNumber.telephone().isNotEmpty()){
        companyTelephone.text = contactNumber.telephone()
        companyTelephone.visibility = View.VISIBLE
    }
    if (career.jobDescription.isNotEmpty()){
        jobDescription.text = career.jobDescription
        jobDescription.visibility = View.VISIBLE
    }
}