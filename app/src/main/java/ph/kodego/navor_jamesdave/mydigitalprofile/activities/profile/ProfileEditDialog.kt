package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProfileEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.updateInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class ProfileEditDialog<T>(context: T, private val profile: Profile): AlertDialog(context) where T: Context, T: ViewModelStoreOwner{
    private val binding by lazy { DialogueProfileEditBinding.inflate(layoutInflater) }
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(context)[ProfileViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

        setProfileDetails()

        with(binding.editButtons){
            updateInterface()
            btnUpdate.setOnClickListener { validateForm() }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    override fun show() {
        super.show()
        binding.profession.requestFocus()
    }

    private fun validateForm() {
        val profession = binding.profession.text.toString().trim()
        val summary = binding.profileSummary.text.toString().trim()
        if (profession.isNotEmpty()){
            val updatedProfile = profile.copy(profession =  profession, profileSummary =  summary)
            checkChanges(updatedProfile)
        }else{
            binding.profession.error = "Field must not be empty."
            binding.profession.requestFocus()
        }
    }

    private fun checkChanges(updatedProfile: Profile) {
        val changes: HashMap<String, Any?> = HashMap()
        if (profile.profession != updatedProfile.profession) changes[Profile.KEY_PROFESSION] = updatedProfile.profession
        if (profile.profileSummary != updatedProfile.profileSummary) changes[Profile.KEY_PROFILE_SUMMARY] = updatedProfile.profileSummary

        if (changes.isNotEmpty()){
            updateProfile(changes)
        }else{
            Toast.makeText(context, "No Fields Changed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfile(changes: Map<String, Any?>) {
        val progressDialog = ProgressDialog(context, R.string.updating_profile).apply { show() }
        CoroutineScope(IO).launch {
            val updateSuccessful = viewModel.updateProfile(profile, changes)
            withContext(Main){
                if (updateSuccessful){
                    Toast.makeText(context, "Update Successful!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Update Failed!", Toast.LENGTH_SHORT).show()
                }
                progressDialog.dismiss()
                dismiss()
            }
        }
    }

    private fun setProfileDetails() {
        with(binding) {
            profession.setText(profile.profession)
            profileSummary.setText(profile.profileSummary)
        }
    }
}