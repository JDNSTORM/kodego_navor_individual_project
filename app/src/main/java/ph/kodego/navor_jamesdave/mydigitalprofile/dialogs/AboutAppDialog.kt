package ph.kodego.navor_jamesdave.mydigitalprofile.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.OnClickListener
import androidx.appcompat.app.AlertDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueAboutAppBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles

class AboutAppDialog(context: Context): AlertDialog(context) {
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
            val profileID = getString(R.string.profile_id)
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(IntentBundles.ProfileID, profileID)
            startActivity(intent)
        }
        dismiss()
    }
}