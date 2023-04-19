package ph.kodego.navor_jamesdave.mydigitalprofile.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProgressBinding

class ProgressDialog(context: Context, private val progressText: Int = R.string.please_wait): Dialog(context) {
    private lateinit var binding: DialogueProgressBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogueProgressBinding.inflate(layoutInflater)
        binding.progressText.setText(progressText)
        setContentView(binding.root)
        setCancelable(false)
    }
}