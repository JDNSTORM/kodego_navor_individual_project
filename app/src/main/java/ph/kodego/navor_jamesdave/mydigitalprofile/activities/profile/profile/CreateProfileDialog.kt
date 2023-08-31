package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogCreateProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutEditButtonsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface

class CreateProfileDialog(
    context: Context,
    private val createProfile: (String) -> Unit
): MaterialAlertDialogBuilder(context) {
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogCreateProfileBinding

    override fun create(): AlertDialog {
        binding = DialogCreateProfileBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        setCancelable(false)
        return super.create().also {
            dialog = it
            binding.editButtons.setupButtons()
        }
    }

    private fun LayoutEditButtonsBinding.setupButtons(){
        saveInterface()
        btnSave.setOnClickListener { binding.checkForm() }
        btnCancel.setOnClickListener { dialog.dismiss() }
    }

    private fun DialogCreateProfileBinding.checkForm(){
        val title = profession.text.toString()
        if (title.isNotEmpty()){
            createProfile(title)
            dialog.dismiss()
        }else{
            profession.error = "Profession must not be empty"
            profession.requestFocus()
        }
    }
}