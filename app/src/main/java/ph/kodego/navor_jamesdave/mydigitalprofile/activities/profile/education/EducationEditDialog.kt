package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.education

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.DeleteItemDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogEducationEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.editInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class EducationEditDialog(
    context: Context,
    private val profile: Profile,
    private val update: (Map<String, Any?>) -> Unit
): AlertDialog(context){
    private val binding by lazy { DialogEducationEditBinding.inflate(layoutInflater) }
    private val educations: ArrayList<Education> = ArrayList()
    private var education: Education? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        with(binding.editButtons) {
            btnCancel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener { saveEducation() }
            btnUpdate.setOnClickListener { updateEducation() }
            btnDelete.setOnClickListener { openDeleteDialog() }
        }
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
        dismiss()
        educations.lastIndex
        val changes = mapOf<String, Any?>(Profile.KEY_EDUCATIONS to educations)
        update(changes)
//        CoroutineScope(IO).launch {
//            val updateSuccessful = viewModel.updateProfile(profile, changes)
//            withContext(Main){
//                if (updateSuccessful){
//                    Toast.makeText(context, "Educations Updated!", Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(context, "Save Failed!", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
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

    override fun show() {
        super.show()
        binding.editButtons.saveInterface()
        binding.degree.requestFocus()
    }

    fun edit(education: Education, list: List<Education>){
        super.show()
        this.education = education
        binding.setEducationDetails()
        list.lastIndex
        educations.clear()
        educations.addAll(list)
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