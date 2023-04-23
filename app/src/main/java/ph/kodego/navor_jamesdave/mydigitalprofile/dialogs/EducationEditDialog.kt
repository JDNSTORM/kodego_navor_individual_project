package ph.kodego.navor_jamesdave.mydigitalprofile.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.RVEducationsAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewHolder
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueEducationEditBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.bind
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.clear
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseEducationDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Education

class EducationEditDialog(
    context: Context,
    private val dao: FirebaseEducationDAOImpl,
    private val rvAdapter: RVEducationsAdapter
): AlertDialog(context) {
    private lateinit var binding: DialogueEducationEditBinding
    private val progressDialog: ProgressDialog = ProgressDialog(context)
    private var education: Education? = null
    private var holder: ViewHolder? = null
    private val lifecycleScope = CoroutineScope(Dispatchers.Main.immediate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogueEducationEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)
        setOnDismissListener {
            education = null
            holder = null
            binding.clear()
        }
        with(binding.editButtons) {
            btnCancel.setOnClickListener { dismiss() }
            btnSave.setOnClickListener {  }
            btnUpdate.setOnClickListener {  }
            btnDelete.setOnClickListener {  }
        }
    }

    override fun show() {
        super.show()
        binding.dateEnrolled.requestFocus()
    }

    fun show(education: Education, holder: ViewHolder) {
        super.show()
        this.education = education
        this.holder = holder
        binding.bind(education)
    }
}