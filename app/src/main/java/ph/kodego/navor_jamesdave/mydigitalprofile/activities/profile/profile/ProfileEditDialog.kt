package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogProfileEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutEditButtonsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.updateInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

class ProfileEditDialog(
    context: Context,
    private val profile: Profile,
    private val update: (Map<String, Any?>) -> StateFlow<RemoteState>
): MaterialAlertDialogBuilder(context){
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogProfileEditBinding

    override fun create(): AlertDialog {
        binding = DialogProfileEditBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        setCancelable(false)
        binding.setProfileDetails()
        return super.create().also {
            dialog = it
            binding.profession.requestFocus()
            binding.editButtons.setupButtons()
        }
    }

    private fun LayoutEditButtonsBinding.setupButtons(){
        updateInterface()
        btnUpdate.setOnClickListener { binding.validateForm() }
        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    private fun DialogProfileEditBinding.validateForm() {
        val professionText = profession.text.toString().trim()
        val summaryText = profileSummary.text.toString().trim()
        if (professionText.isNotEmpty()){
            val updatedProfile = profile.copy(profession =  professionText, profileSummary =  summaryText)
            checkChanges(updatedProfile)
            dialog.dismiss()
        }else{
            profession.error = "Field must not be empty."
            profession.requestFocus()
        }
    }

    private fun monitorState(state: StateFlow<RemoteState>){
        val progressDialog = ProgressDialog(context, R.string.updating_profile).create()
        CoroutineScope(Main).launch {
            state.collect{
                when(it){
                    RemoteState.Waiting -> progressDialog.show()
                    RemoteState.Success -> Toast.makeText(context, "Update Successful!", Toast.LENGTH_SHORT).show()
                    RemoteState.Failed -> Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show()
                    RemoteState.Invalid -> Toast.makeText(context, "Unexpected Error!", Toast.LENGTH_SHORT).show()
                    RemoteState.Idle -> {
                        progressDialog.dismiss()
                        dialog.dismiss()
                    }
                }
            }
        }
    }

    private fun checkChanges(updatedProfile: Profile) {
        val changes: HashMap<String, Any?> = HashMap()
        if (profile.profession != updatedProfile.profession) changes[Profile.KEY_PROFESSION] = updatedProfile.profession
        if (profile.profileSummary != updatedProfile.profileSummary) changes[Profile.KEY_PROFILE_SUMMARY] = updatedProfile.profileSummary

        if (changes.isNotEmpty()){
//            monitorState(
                update(changes)
//            )
        }else{
            Toast.makeText(context, "No Fields Changed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun DialogProfileEditBinding.setProfileDetails() {
        profession.setText(profile.profession)
        profileSummary.setText(profile.profileSummary)
    }
}