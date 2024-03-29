package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.DeleteItemDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogCareerEditBinding
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
): AlertDialog(context) {
    private val binding by lazy { DialogCareerEditBinding.inflate(layoutInflater) }
    private var careers: ArrayList<Career> = ArrayList()
    private var career: Career? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        with(binding.editButtons) {
            btnCancel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener { saveCareer() }
            btnUpdate.setOnClickListener { updateCareer() }
            btnDelete.setOnClickListener { openDeleteDialog() }
        }
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
        dismiss()
//        CoroutineScope(IO).launch {
//            val updateSuccessful = viewModel.updateProfile(profile, changes)
//            withContext(Main){
//                if (updateSuccessful){
//                    Toast.makeText(context, "Careers Updated!", Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(context, "Save Failed!", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
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

    override fun show() {
        super.show()
        binding.editButtons.saveInterface()
        binding.dateEmployed.requestFocus()
    }

    fun edit(career: Career, list: List<Career>){
        super.show()
        this.career = career
        setCareerDetails()
        list.lastIndex
        careers.clear()
        careers.addAll(list)
        Log.d("Careers", list.toList().size.toString())
        Log.d("CareerEdit", career.hashCode().toString())
        list.toList().forEachIndexed { index, item ->
            Log.d("Career", "$index: ${item.hashCode()}")
        }
    }

    private fun setCareerDetails(){
        with(binding){
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
}