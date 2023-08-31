package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.DeleteItemDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogCareerEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutEditButtonsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.editInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

class CareerEditDialog(
    context: Context,
    private val profile: Profile,
    private val update: (Map<String, Any?>)  -> Unit
): MaterialAlertDialogBuilder(context) {
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogCareerEditBinding
    private var careers: ArrayList<Career> = ArrayList()
    private var career: Career? = null

    private fun LayoutEditButtonsBinding.setupButtons(){
        btnCancel.setOnClickListener { dialog.dismiss() }
        btnSave.setOnClickListener { saveCareer() }
        btnUpdate.setOnClickListener { updateCareer() }
        btnDelete.setOnClickListener { openDeleteDialog() }
    }

    private fun openDeleteDialog() {
        DeleteItemDialog(context, career!!){ deleteCareer() }.show()
    }

    private fun deleteCareer() {
        careers.remove(career!!)
        saveChanges(careers)
    }

    private fun updateCareer() {
        getFormData()?.let {
            careers[careers.indexOf(career!!)] = it
            saveChanges(careers)
        }
    }

    private fun saveCareer() {
        getFormData()?.let {
            profile.careers.add(it)
            saveChanges(profile.careers)
        }
    }

    private fun saveChanges(careers: List<Career>){
        careers.lastIndex
        val changes = mapOf<String, Any?>(Profile.KEY_CAREERS to careers)
        update(changes)
        dialog.dismiss()
    }

    private fun getFormData(): Career? {
        var valid = true
        val employmentStart = binding.dateEmployed.text.toString().trim()
        val employmentEnd = binding.employmentEnd.text.toString().trim()
        val position = binding.position.text.toString().trim()
        val company = binding.company.text.toString().trim()
        val jobDescription = binding.jobDescription.text.toString().trim()
        val email = "" //TODO
        val companyAddress = Address(binding.companyAddress.text.toString().trim())
        val website = binding.companyWebsite.text.toString().trim()
        val telAreaCode = binding.layoutContactEdit.telAreaCode.text.toString().trim()
        val telNumber = binding.layoutContactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0
        val contactNumber = ContactNumber(telAreaCode, telNumber)
        if(company.isEmpty()){
            valid = false
            binding.company.error = "This Field is required."
            binding.company.requestFocus()
        }
        if (position.isEmpty()) {
            valid = false
            binding.position.error = "This Field is required!"
            binding.position.requestFocus()
        }
        return if (valid){
            Career(
                company,
                position,
                employmentStart,
                employmentEnd,
                jobDescription,
                email,
                companyAddress,
                contactNumber,
                website
            )
        }else{
            null
        }
    }

    override fun create(): AlertDialog {
        binding = DialogCareerEditBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        setCancelable(false)
        binding.setCareerDetails()
        return super.create().also {
            dialog = it
            binding.editButtons.setupButtons()
            binding.editButtons.saveInterface()
            binding.dateEmployed.requestFocus()
        }
    }

    fun edit(career: Career, list: List<Career>){
        this.career = career
        list.lastIndex
        careers.clear()
        careers.addAll(list)
        Log.d("Careers", list.toList().size.toString())
        Log.d("CareerEdit", career.hashCode().toString())
        list.toList().forEachIndexed { index, item ->
            Log.d("Career", "$index: ${item.hashCode()}")
        }
        show()
    }

    private fun DialogCareerEditBinding.setCareerDetails(){
        career?.let {
            dateEmployed.setText(it.employmentStart)
            employmentEnd.setText(it.employmentEnd)
            position.setText(it.position)
            company.setText(it.companyName)
            jobDescription.setText(it.jobDescription)
            companyAddress.setText(it.address.streetAddress)
            companyWebsite.setText(it.website)
            layoutContactEdit.telAreaCode.setText(it.contactNumber.areaCode)
            layoutContactEdit.telContactNumber.setText(it.contactNumber.contact.toString())

            editButtons.editInterface()
        } ?: editButtons.saveInterface()
    }
}