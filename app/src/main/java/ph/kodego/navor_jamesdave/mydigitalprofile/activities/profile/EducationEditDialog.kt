package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

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
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueEducationEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.editInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class EducationEditDialog<T>(context: T, private val profile: Profile): AlertDialog(context) where T: Context, T: ViewModelStoreOwner{
    private val binding by lazy { DialogueEducationEditBinding.inflate(layoutInflater) }
    private val viewModel by lazy { ViewModelProvider(context)[ProfileViewModel::class.java] }
    private var educations: ArrayList<Education> = ArrayList()
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
        object: DeleteItemDialog(context, education!!){
            override fun ifYes(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                deleteEducation()
                dialog.dismiss()
            }
        }.show()
    }

    private fun deleteEducation() {
        val educations = educations.apply { remove(education!!) }
        saveChanges(educations)
    }

    private fun updateEducation() {
        getFormData()?.let {
            val educations = educations.apply { this[indexOf(education!!)] = it }
            saveChanges(educations)
        }
    }

    private fun saveEducation() {
        getFormData()?.let {
            val educations = profile.educations.apply { add(it) }
            saveChanges(educations)
        }
    }

    private fun saveChanges(educations: List<Education>) {
        dismiss()
        educations.lastIndex
        val changes = mapOf<String, Any?>(Profile.KEY_EDUCATIONS to educations)
        CoroutineScope(IO).launch {
            val updateSuccessful = viewModel.updateProfile(profile, changes)
            withContext(Main){
                if (updateSuccessful){
                    Toast.makeText(context, "Educations Updated!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Save Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
        setEducationDetails()
        list.lastIndex
        educations.clear()
        educations.addAll(list)
    }

    private fun setEducationDetails() {
        with(binding) {
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
}