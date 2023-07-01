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
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogAboutAppBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class AboutAppDialog(
    context: Context,
    private val viewDeveloperProfile: () -> Unit
): AlertDialog(context){

    private val binding by lazy {
        DialogAboutAppBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding){
            btnViewGit.setOnClickListener { openGitRepositoryURL() }
            btnViewProfile.setOnClickListener {
                viewDeveloperProfile()
                dismiss()
            }
            btnClose.setOnClickListener { dismiss() }
        }
    }

    private fun openGitRepositoryURL(){
        val url = Uri.parse("https://github.com/JDNSTORM/kodego_navor_individual_project.git")
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = url
        context.startActivity(openURL)
    }
}