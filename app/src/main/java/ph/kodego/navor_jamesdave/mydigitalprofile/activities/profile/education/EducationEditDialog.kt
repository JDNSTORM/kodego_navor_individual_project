package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.education

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.DeleteItemDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogEducationEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutEditButtonsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.editInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

class EducationEditDialog(
    context: Context,
    private val profile: Profile,
    private val update: (Map<String, Any?>) -> Unit
): MaterialAlertDialogBuilder(context){
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogEducationEditBinding
    private val educations: ArrayList<Education> = ArrayList()
    private var education: Education? = null

    override fun create(): AlertDialog {
        binding = DialogEducationEditBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        setCancelable(false)
        binding.editButtons.setupButtons()
        setOnDismissListener {
            education = null
            educations.clear()
        }
        binding.setEducationDetails()
        return super.create().also {
            dialog = it
        }
    }

    private fun LayoutEditButtonsBinding.setupButtons(){
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener { saveEducation() }
        btnUpdate.setOnClickListener { updateEducation() }
        btnDelete.setOnClickListener { openDeleteDialog() }
    }

    private fun openDeleteDialog() {
        DeleteItemDialog(context, education!!){
            deleteEducation()
        }.show()
    }

    private fun deleteEducation() {
        educations.remove(education!!)
        saveChanges(educations)
    }

    private fun updateEducation() {
        getFormData()?.let {
            educations[educations.indexOf(education!!)] = it
            saveChanges(educations)
        }
    }

    private fun saveEducation() {
        getFormData()?.let {
            profile.educations.add(it)
            saveChanges(profile.educations)
        }
    }

    private fun saveChanges(educations: List<Education>) {
        dialog.dismiss()
        educations.lastIndex
        val changes = mapOf<String, Any?>(Profile.KEY_EDUCATIONS to educations)
        update(changes)
    }

    private fun getFormData(): Education? {
        var valid = true
        with(binding) {
            val schoolName = schoolName.text.toString().trim()
            val degree = degree.text.toString().trim()
            val fieldOfStudy = fieldOfStudy.text.toString().trim()
            val dateEnrolled = dateEnrolled.text.toString().trim()
            val dateGraduated = dateGraduated.text.toString().trim()
            val email = "" //TODO
            val schoolAddress = schoolAddress.text.toString().trim()
            val telAreaCode = contactEdit.telAreaCode.text.toString().trim()
            val telNumber = contactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0
            val website = schoolWebsite.text.toString().trim()

            if (schoolName.isEmpty()){
                valid = false
                this.schoolName.error = "This Field is Required."
                this.schoolName.requestFocus()
            }
            if (fieldOfStudy.isEmpty()){
                valid = false
                this.fieldOfStudy.error = "This Field is Required."
                this.fieldOfStudy.requestFocus()
            }

            return if (valid){
                val address = Address(schoolAddress)
                val contactNumber = ContactNumber(telAreaCode, telNumber)
                Education(schoolName, degree, fieldOfStudy, dateEnrolled, dateGraduated, email, address, contactNumber, website)
            }else{null}
        }
    }

    override fun show(): AlertDialog? {
        return super.show().also {
            binding.degree.requestFocus()
        }
    }

    fun edit(education: Education, list: List<Education>){
        this.education = education
        list.lastIndex
        educations.clear()
        educations.addAll(list)
        show()
    }

    private fun DialogEducationEditBinding.setEducationDetails() {
        education?.let { education ->
            dateEnrolled.setText(education.dateEnrolled)
            dateGraduated.setText(education.dateGraduated)
            schoolName.setText(education.schoolName)
            schoolAddress.setText(education.address.streetAddress)
            schoolWebsite.setText(education.website)
            contactEdit.telAreaCode.setText(education.contactNumber.areaCode)
            contactEdit.telContactNumber.setText(education.contactNumber.contact.toString())
            degree.setText(education.degree)
            fieldOfStudy.setText(education.fieldOfStudy)

            editButtons.editInterface()
        } ?: editButtons.saveInterface()
    }
}