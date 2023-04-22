package ph.kodego.navor_jamesdave.mydigitalprofile.utils

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVCareersAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewHolder
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueCareerEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseCareerDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Career
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactInformation
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Website

class CareerEditDialog(context: Context, private val dao: FirebaseCareerDAOImpl, private val adapter: RVCareersAdapter): AlertDialog(context) {
    private lateinit var binding: DialogueCareerEditBinding
    private val progressDialog: ProgressDialog = ProgressDialog(context)
    private var career: Career? = null
    private var holder: ViewHolder? = null
    private val lifecycleScope = CoroutineScope(Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogueCareerEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
        setOnDismissListener {
            career = null
            holder = null
            binding.clear()
        }
        with(binding.editButtons) {
            btnCancel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener { saveCareer() }
            btnUpdate.setOnClickListener { updateCareer() }
        }
    }

    override fun show() {
        super.show()
        binding.dateEmployed.requestFocus()
    }

    fun show(career: Career, holder: ViewHolder) {
        this.career = career
        this.holder = holder
        binding.bind(career)
        super.show()
    }

    private fun saveCareer(){
        val career = Career(dao.profileID())
        with(binding) {
            career.employmentStart = dateEmployed.text.toString().trim()
            career.employmentEnd = employmentEnd.text.toString().trim()
            career.position = position.text.toString().trim()
            career.companyName = company.text.toString().trim()
            val contactInformation = ContactInformation()
            val address = Address()
            address.streetAddress = streetAddress.text.toString().trim()
            address.subdivision = subdivision.text.toString().trim()
            address.cityOrMunicipality = city.text.toString().trim()
            address.zipCode = zipCode.text.toString().toIntOrNull() ?: 0
            address.province = province.text.toString().trim()
            address.country = country.text.toString().trim()
            contactInformation.address = address
            val companyWebsite = companyWebsite.text.toString().trim()
            if (companyWebsite.isNotEmpty()) {
                val website = Website()
                website.website = companyWebsite
                contactInformation.website = website
            }
            val telephone = ContactNumber()
            telephone.areaCode = layoutContactEdit.telAreaCode.text.toString().trim()
            telephone.contact =
                layoutContactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0
            contactInformation.contactNumber = telephone
            career.contactInformation = contactInformation
            career.jobDescription = jobDescription.text.toString()
        }
        addCareer(career)
        dismiss()
    }

    private fun addCareer(career: Career){
        progressDialog.show()
        lifecycleScope.launch {
            if(dao.addCareer(career)){
                adapter.addCareer(career)
                Toast.makeText(context, "Career added successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Error Adding Career", Toast.LENGTH_SHORT).show()
            }
            progressDialog.dismiss()
        }
    }

    private fun updateCareer(){
        val updatedCareer = Career(career!!)
        val updatedAddress = Address(career!!.contactInformation!!.address!!)
        with(binding) {
            updatedCareer.employmentStart = dateEmployed.text.toString().trim()
            updatedCareer.employmentEnd = employmentEnd.text.toString().trim()
            updatedCareer.position = position.text.toString().trim()
            updatedCareer.companyName = company.text.toString().trim()
            val contactInformation = ContactInformation()
            val address = Address()
            address.streetAddress = streetAddress.text.toString().trim()
            address.subdivision = subdivision.text.toString().trim()
            address.cityOrMunicipality = city.text.toString().trim()
            address.zipCode = zipCode.text.toString().toIntOrNull() ?: 0
            address.province = province.text.toString().trim()
            address.country = country.text.toString().trim()
            contactInformation.address = address
            val companyWebsite = companyWebsite.text.toString().trim()
            if (companyWebsite.isNotEmpty()) {
                val website = Website()
                website.website = companyWebsite
                contactInformation.website = website
            }
            val telephone = ContactNumber()
            telephone.areaCode = layoutContactEdit.telAreaCode.text.toString().trim()
            telephone.contact =
                layoutContactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0
            contactInformation.contactNumber = telephone
            updatedCareer.contactInformation = contactInformation
            updatedCareer.jobDescription = jobDescription.text.toString()
        }
    }
}