package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career.CreateProfileDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.AccountProfilesAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogProfileSelectBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class SelectProfileDialog<T>(context: T): AlertDialog(context), FlowCollector<List<Profile>> where T: Context, T: ViewModelStoreOwner{
    private val binding by lazy { DialogProfileSelectBinding.inflate(layoutInflater) }
    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(context)[ProfileViewModel::class.java]
    }
    private val itemsAdapter by lazy { AccountProfilesAdapter(viewModel, this) }
    private val exitDialog by lazy {
        object: ExitWarningDialog(context){
            override fun ifYes(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                this@SelectProfileDialog.dismiss()
                dialog.dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)

        setupRecyclerView()
        with(binding) {
            btnCreateProfile.setOnClickListener {
                CreateProfileDialog(
                    context,
                    viewModel
                ).show()
            }
            btnClose.setOnClickListener { exitDialog.show() }
        }

    }

    private fun setupRecyclerView() {
        CoroutineScope(Main).launch {
            viewModel.readAccountProfiles().collect(this@SelectProfileDialog)
        }
        with(binding.listProfiles){
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
        }
    }

    override suspend fun emit(value: List<Profile>) {
        itemsAdapter.setList(value)
    }
}