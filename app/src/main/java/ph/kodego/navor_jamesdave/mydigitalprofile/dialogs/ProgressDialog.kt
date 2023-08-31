package ph.kodego.navor_jamesdave.mydigitalprofile.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogProgressBinding

class ProgressDialog(context: Context, private val progressText: Int = R.string.please_wait): MaterialAlertDialogBuilder(context) {
    private val binding = DialogProgressBinding.inflate(LayoutInflater.from(context))
    init {
        setView(binding.root)
        binding.progressText.setText(progressText)
        setCancelable(false)
    }
}