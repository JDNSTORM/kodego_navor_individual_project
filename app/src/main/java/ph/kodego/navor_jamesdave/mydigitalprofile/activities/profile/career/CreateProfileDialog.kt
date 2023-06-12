package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogCreateProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.saveInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class CreateProfileDialog(context: Context, private val viewModel: ProfileViewModel): AlertDialog(context) {
    private val binding by lazy { DialogCreateProfileBinding.inflate(layoutInflater) }
    private val progressDialog by lazy { ProgressDialog(context, R.string.please_wait) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window!!.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        with(binding.editButtons) {
            saveInterface()
            btnSave.setOnClickListener { checkForm() }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    override fun show() {
        super.show()
        binding.profession.text?.clear()
    }

    private fun checkForm(){
        val profession = binding.profession.text.toString()
        val isPublic = binding.isPublic.isChecked
        if (profession.isNotEmpty()){
            saveProfile(profession, isPublic)
        }else{
            binding.profession.error = "Profession must not be empty"
            binding.profession.requestFocus()
        }
    }

    private fun saveProfile(profession: String, isPublic: Boolean) {
        progressDialog.show()
        CoroutineScope(IO).launch {
            val saveSuccessful = viewModel.addProfile(profession, isPublic)
            withContext(Main){
                if (!saveSuccessful){
                    Toast.makeText(context, "Failed to create Profile!", Toast.LENGTH_SHORT).show()
                }
                progressDialog.dismiss()
                dismiss()
            }
        }
    }
}