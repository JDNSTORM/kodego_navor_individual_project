package ph.kodego.navor_jamesdave.mydigitalprofile.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View.OnClickListener
import androidx.appcompat.app.AlertDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueAboutAppBinding

class AboutAppDialog(context: Context): AlertDialog(context) {
    private lateinit var binding: DialogueAboutAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogueAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClose.setOnClickListener { dismiss() }
    }

    fun onViewGitRepository(listener: OnClickListener){
        binding.btnViewGit.setOnClickListener(listener)
    }

    fun onViewProfile(listener: OnClickListener){
        binding.btnViewProfile.setOnClickListener(listener)
    }
}