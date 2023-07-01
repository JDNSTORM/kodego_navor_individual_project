package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogProfileEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.updateInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

class ProfileEditDialog(
    context: Context,
    private val profile: Profile,
    private val update: (Map<String, Any?>) -> StateFlow<RemoteState>
): AlertDialog(context){
    private val binding by lazy { DialogProfileEditBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        setProfileDetails()

        with(binding.editButtons){
            updateInterface()
            btnUpdate.setOnClickListener { binding.validateForm() }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    override fun show() {
        super.show()
        binding.profession.requestFocus()
    }

    private fun DialogProfileEditBinding.validateForm() {
        val professionText = profession.text.toString().trim()
        val summaryText = profileSummary.text.toString().trim()
        if (professionText.isNotEmpty()){
            val updatedProfile = profile.copy(profession =  professionText, profileSummary =  summaryText)
            checkChanges(updatedProfile)
        }else{
            profession.error = "Field must not be empty."
            profession.requestFocus()
        }
    }

    private fun monitorState(state: StateFlow<RemoteState>){
        val progressDialog = ProgressDialog(context, R.string.updating_profile)
        CoroutineScope(Main).launch {
            state.collect{
                when(it){
                    RemoteState.Waiting -> progressDialog.show()
                    RemoteState.Success -> Toast.makeText(context, "Update Successful!", Toast.LENGTH_SHORT).show()
                    RemoteState.Failed -> Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show()
                    RemoteState.Invalid -> Toast.makeText(context, "Unexpected Error!", Toast.LENGTH_SHORT).show()
                    RemoteState.Idle -> {
                        progressDialog.dismiss()
                        dismiss()
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
            monitorState(update(changes))
        }else{
            Toast.makeText(context, "No Fields Changed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile(changes: Map<String, Any?>) {
//        val progressDialog = ProgressDialog(context, R.string.updating_profile).apply { show() }
//        CoroutineScope(IO).launch {
//            val updateSuccessful = viewModel.updateProfile(profile, changes)
//            withContext(Main){
//                if (updateSuccessful){
//                    Toast.makeText(context, "Update Successful!", Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show()
//                }
//                progressDialog.dismiss()
//                dismiss()
//            }
//        }
    }

    private fun setProfileDetails() {
        with(binding) {
            profession.setText(profile.profession)
            profileSummary.setText(profile.profileSummary)
        }
    }
}