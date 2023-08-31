package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogAboutAppBinding

class AboutAppDialog(
    context: Context,
    private val viewDeveloperProfile: () -> Unit
): MaterialAlertDialogBuilder(context){
    private lateinit var dialog: AlertDialog
    private val binding = DialogAboutAppBinding.inflate(LayoutInflater.from(context))

    init {
        setView(binding.root)
    }

    override fun show(): AlertDialog {
        dialog = super.show()
        binding.setupUI()
        return dialog
    }

    private fun DialogAboutAppBinding.setupUI(){
        btnViewGit.setOnClickListener { openGitRepositoryURL() }
        btnViewProfile.setOnClickListener {
            viewDeveloperProfile()
            dialog.dismiss()
        }
        btnClose.setOnClickListener{
            dialog.dismiss()
        }
    }

    private fun openGitRepositoryURL(){
        val url = Uri.parse("https://github.com/JDNSTORM/kodego_navor_individual_project.git")
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = url
        context.startActivity(openURL)
    }
}