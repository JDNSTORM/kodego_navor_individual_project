package ph.kodego.navor_jamesdave.mydigitalprofile.dialogs

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProfileEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.bind
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.updateInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfessionalSummaryDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfessionalSummary
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls

class ProfileEditDialog(context: Context, private val dao: FirebaseProfessionalSummaryDAOImpl): AlertDialog(context) {
    private lateinit var binding: DialogueProfileEditBinding
    private val progressDialog: ProgressDialog = ProgressDialog(context)
    lateinit var profile: Profile
    lateinit var professionalSummary: ProfessionalSummary
    var updated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogueProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        with(binding.editButtons){
            updateInterface()
            btnUpdate.setOnClickListener { updateProfile() }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    /**
     * Disable default show
     */
    override fun show() {}
    fun show(profile: Profile, professionalSummary: ProfessionalSummary){
        this.profile = profile
        this.professionalSummary = professionalSummary
        updated = false
        super.show()
        binding.bind(profile, professionalSummary)
    }

    private fun updateProfile(){
        val updatedProfile = profile.exportFirebaseProfile()
        val updatedProfessionalSummary = professionalSummary.copy()
        updatedProfessionalSummary.setSummary(professionalSummary)
        with(binding){
            updatedProfile.profession = profession.text.toString().trim()
            updatedProfessionalSummary.profileSummary = profileSummary.text.toString().trim()
        }
        val updatedProfileFields: HashMap<String, Any?> = FormControls().getModified(profile.exportFirebaseProfile(), updatedProfile)
        val updatedSummaryFields: HashMap<String, Any?> = FormControls().getModified(professionalSummary, updatedProfessionalSummary)
        if (updatedProfileFields.isNotEmpty() || updatedSummaryFields.isNotEmpty()){
            progressDialog.show()
            lifecycleScope.launch {
                if (dao.updateProfile(profile, updatedProfileFields) && dao.updateProfessionalSummary(professionalSummary, updatedSummaryFields)){
                    profile.importFirebaseProfile(updatedProfile)
                    professionalSummary = updatedProfessionalSummary

                    updated = true
                    Toast.makeText(context, "Profile and Summary updated", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Error Updating Profile and Summary", Toast.LENGTH_SHORT).show()
                }
                dismiss()
                progressDialog.dismiss()
            }
        }else{
            Toast.makeText(context, "No fields have been changed", Toast.LENGTH_SHORT).show()
        }
    }
}