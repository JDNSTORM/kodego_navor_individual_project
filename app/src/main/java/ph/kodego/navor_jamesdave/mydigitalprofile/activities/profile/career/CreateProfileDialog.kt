package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogCreateProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface

class CreateProfileDialog(
    context: Context,
    private val createProfile: (String) -> Unit
): AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        with(binding.editButtons) {
            saveInterface()
            btnSave.setOnClickListener { binding.checkForm() }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    private fun DialogCreateProfileBinding.checkForm(){
        val title = profession.text.toString()
        if (title.isNotEmpty()){
            createProfile(title)
            dismiss() //TODO: ActionState
        }else{
            profession.error = "Profession must not be empty"
            profession.requestFocus()
        }
    }
}