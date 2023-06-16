package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueAboutAppBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class AboutAppDialog<T>(context: T): AlertDialog(context) where T: Context, T: ViewModelStoreOwner{
    private val viewModel by lazy { ViewModelProvider(context)[ProfileViewModel::class.java] }

    private val binding: DialogueAboutAppBinding by lazy {
        DialogueAboutAppBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            btnViewGit.setOnClickListener { openGitRepositoryURL() }
            btnViewProfile.setOnClickListener { viewDeveloperProfile() }
            btnClose.setOnClickListener { dismiss() }
        }
    }

    private fun openGitRepositoryURL(){
        val url = Uri.parse("https://github.com/JDNSTORM/kodego_navor_individual_project.git")
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = url
        context.startActivity(openURL)
    }

    private fun viewDeveloperProfile(){
        with(context) {
            val accountID = getString(R.string.account_id)
            val profileID = getString(R.string.profile_id)
            val profile = Profile(profileID = profileID, refUID = accountID)
            val intent = Intent(this, ProfileActivity::class.java)
            viewModel.setActiveProfile(profile)
            startActivity(intent)
        }
        dismiss()
    }
}