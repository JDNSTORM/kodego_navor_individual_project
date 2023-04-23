package ph.kodego.navor_jamesdave.mydigitalprofile.dialogs

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
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.bind
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.clear
//TODO: Find a Workaround to enable SoftInputMode
class CareerEditDialog(context: Context, private val dao: FirebaseCareerDAOImpl, private val adapter: RVCareersAdapter): AlertDialog(context) {
    private lateinit var binding: DialogueCareerEditBinding
    private val progressDialog: ProgressDialog = ProgressDialog(context)
    private var career: Career? = null
    private var holder: ViewHolder? = null
    private val lifecycleScope = CoroutineScope(Dispatchers.Main.immediate)

//    init {
//        create()
//    }

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
            btnUpdate.setOnClickListener { checkUpdates() }
            btnDelete.setOnClickListener { deleteCareer() }
        }
    }

    override fun show() {
        super.show()
        binding.dateEmployed.requestFocus()
    }

    fun show(career: Career, holder: ViewHolder) {
        super.show()
        this.career = career
        this.holder = holder
        binding.bind(career)
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
            address.streetAddress = streetAddress.text.toString().trim() //TODO: Only use Street Address
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
            career.jobDescription = jobDescription.text.toString().trim()
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

    private fun checkUpdates(){
        val updatedCareer = Career(career!!)
        val contactInformation = career!!.contactInformation!!
        val updatedAddress = Address(contactInformation.address!!)
        val updatedWebsite = Website(contactInformation.website!!)
        val updatedTelephone = ContactNumber(contactInformation.contactNumber!!)
        with(binding) {
            updatedCareer.employmentStart = dateEmployed.text.toString().trim()
            updatedCareer.employmentEnd = employmentEnd.text.toString().trim()
            updatedCareer.position = position.text.toString().trim()
            updatedCareer.companyName = company.text.toString().trim()

            updatedAddress.streetAddress = streetAddress.text.toString().trim()
            updatedAddress.subdivision = subdivision.text.toString().trim()
            updatedAddress.cityOrMunicipality = city.text.toString().trim()
            updatedAddress.zipCode = zipCode.text.toString().toIntOrNull() ?: 0
            updatedAddress.province = province.text.toString().trim()
            updatedAddress.country = country.text.toString().trim()

            updatedWebsite.website = companyWebsite.text.toString().trim()

            updatedTelephone.areaCode = layoutContactEdit.telAreaCode.text.toString().trim()
            updatedTelephone.contact =
                layoutContactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0

            updatedCareer.jobDescription = jobDescription.text.toString().trim()
        }
        val careerUpdate = FormControls().getModified(career!!, updatedCareer)
        val addressUpdate = FormControls().getModified(contactInformation.address!!, updatedAddress)
        val websiteUpdate = FormControls().getModified(contactInformation.website!!, updatedWebsite)
        val telUpdate = FormControls().getModified(contactInformation.contactNumber!!, updatedTelephone)
        if (careerUpdate.size > 0 || addressUpdate.size > 0 || websiteUpdate.size > 0 || telUpdate.size > 0){
            val updates: HashMap<String, Any?> = HashMap()
            if (careerUpdate.size > 0 ){
                updates["Career"] = careerUpdate
            }
            if (addressUpdate.size > 0 ){
                updates["Address"] = addressUpdate
            }
            if (websiteUpdate.size > 0 ){
                updates["Website"] = websiteUpdate
            }
            if (telUpdate.size > 0 ){
                updates["ContactNumber"] = telUpdate
            }
            updatedCareer.contactInformation!!.address = updatedAddress
            updatedCareer.contactInformation!!.website = updatedWebsite
            updatedCareer.contactInformation!!.contactNumber = updatedTelephone
            updateCareer(updatedCareer, updates)
        }else{
            Toast.makeText(context, "No Fields Updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateCareer(updatedCareer: Career, fields: HashMap<String, Any?>){
        progressDialog.show()
        lifecycleScope.launch {
            if(dao.updateCareer(career!!, fields)){
                adapter.updateCareer(career!!, updatedCareer, holder!!)
                Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Error Updating Career", Toast.LENGTH_SHORT).show()
            }
            dismiss()
            progressDialog.dismiss()
        }
    }

    private fun deleteCareer(){
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle("Delete Work History?")
            setMessage("Are you sure to delete ${career!!.position} history?")
            setPositiveButton("Yes") { dialog, _ ->
                progressDialog.show()
                lifecycleScope.launch {
                    if(dao.deleteCareer(career!!)){
                        Toast.makeText(context, "${career!!.position} deleted successfully", Toast.LENGTH_SHORT).show()
                        adapter.deleteCareer(career!!, holder!!)
                        dialog.dismiss()
                        dismiss()
                    }else{
                        Toast.makeText(context, "Error deleting career", Toast.LENGTH_SHORT).show()
                    }
                    progressDialog.dismiss()
                }
            }
            setNegativeButton("No"){ dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.show()
    }
}