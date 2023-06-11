package ph.kodego.navor_jamesdave.mydigitalprofile.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProgressBinding

class ProgressDialog(context: Context, private val progressText: Int = R.string.please_wait): Dialog(context) {
    private val binding: DialogueProgressBinding by lazy {
        DialogueProgressBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        setContentView(binding.root)
        binding.progressText.setText(progressText)
        setCancelable(false)
    }
}