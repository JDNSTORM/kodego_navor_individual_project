package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.AccountProfilesAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogProfileSelectBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class SelectProfileDialog(context: Context): AlertDialog(context), FlowCollector<List<Profile>> {
    private val binding by lazy { DialogProfileSelectBinding.inflate(layoutInflater) }
    private val viewModel: ProfileViewModel by lazy {
        val owner = context as AppCompatActivity
        ViewModelProvider(
            owner,
            ViewModelProvider.AndroidViewModelFactory(owner.application)
        )[ProfileViewModel::class.java]
    }
    private val itemsAdapter by lazy { AccountProfilesAdapter(viewModel, this) }
    private val exitDialog by lazy {
        ExitWarningDialog(context).apply {
            ifYes{ _, _ -> this@SelectProfileDialog.dismiss() }
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