package ph.kodego.navor_jamesdave.mydigitalprofile.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVEducationsAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewHolder
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueEducationEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.bind
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.clear
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.getContents
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseEducationDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Education
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls

class EducationEditDialog(
    context: Context,
    private val dao: FirebaseEducationDAOImpl,
    private val adapter: RVEducationsAdapter
): AlertDialog(context) {
    private lateinit var binding: DialogueEducationEditBinding
    private val progressDialog: ProgressDialog = ProgressDialog(context)
    private var education: Education? = null
    private var holder: ViewHolder? = null
    private val lifecycleScope = CoroutineScope(Dispatchers.Main.immediate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogueEducationEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
        setOnDismissListener {
            education = null
            holder = null
            binding.clear()
        }
        with(binding.editButtons) {
            btnCancel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener { saveEducation() }
            btnUpdate.setOnClickListener { checkUpdates() }
            btnDelete.setOnClickListener { deleteEducation() }
        }
    }

    override fun show() {
        super.show()
        binding.dateEnrolled.requestFocus()
    }

    fun show(education: Education, holder: ViewHolder) {
        super.show()
        this.education = education
        this.holder = holder
        binding.bind(education)
    }

    private fun saveEducation(){
        val education = Education(dao.profileID())
        binding.getContents(education)
        addEducation(education)
        dismiss()
    }

    private fun addEducation(education: Education){
        progressDialog.show()
        lifecycleScope.launch{
            if (dao.addEducation(education)){
                adapter.addEducation(education)
                Toast.makeText(context, "Education added successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Error adding education", Toast.LENGTH_SHORT).show()
            }
            progressDialog.dismiss()
        }
    }

    private fun checkUpdates(){
        val updatedEducation = Education(education!!)
        binding.getContents(updatedEducation)
        val contactInformation = education!!.contactInformation!!
        val updatedContactInformation = updatedEducation.contactInformation!!
        val educationUpdate = FormControls().getModified(education!!, updatedEducation)
        val addressUpdate = FormControls().getModified(contactInformation.address!!, updatedContactInformation.address!!)
        val websiteUpdate = FormControls().getModified(contactInformation.website!!, updatedContactInformation.website!!)
        val telUpdate = FormControls().getModified(contactInformation.contactNumber!!, updatedContactInformation.contactNumber!!)
        if (educationUpdate.size > 0 || addressUpdate.size > 0 || websiteUpdate.size > 0 || telUpdate.size > 0){
            val updates: HashMap<String, Any?> = HashMap()
            if (educationUpdate.size > 0 ){
                updates["Career"] = educationUpdate
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
            updateEducation(updatedEducation, updates)
        }else{
            Toast.makeText(context, "No Fields Updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateEducation(updatedEducation: Education, fields: HashMap<String, Any?>){
        progressDialog.show()
        lifecycleScope.launch {
            if(dao.updateEducation(education!!, fields)){
                adapter.updateEducation(education!!, updatedEducation, holder!!)
                Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Error Updating Career", Toast.LENGTH_SHORT).show()
            }
            progressDialog.dismiss()
            dismiss()
        }
    }

    private fun deleteEducation(){
        val builder = AlertDialog.Builder(context)
        builder.apply {
            setTitle("Delete Education Summary?")
            setMessage("Are you sure to delete ${education!!.fieldOfStudy} Summary?")
            setPositiveButton("Yes") { dialog, _ ->
                progressDialog.show()
                lifecycleScope.launch {
                    if(dao.deleteEducation(education!!)){
                        Toast.makeText(context, "${education!!.fieldOfStudy} Summary deleted successfully", Toast.LENGTH_SHORT).show()
                        adapter.deleteEducation(education!!, holder!!)
                        dialog.dismiss()
                        dismiss()
                    }else{
                        Toast.makeText(context, "Error deleting education", Toast.LENGTH_SHORT).show()
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