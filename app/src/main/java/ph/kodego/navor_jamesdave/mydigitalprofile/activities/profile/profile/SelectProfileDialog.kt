package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career.CreateProfileDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.ExitWarningDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ProfileAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.AccountProfilesAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogProfileSelectBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class SelectProfileDialog(
    context: Context,
    private val accountProfiles: Flow<List<Profile>>,
    private val action: (ProfileAction) -> StateFlow<RemoteState>?,
): AlertDialog(context){
    private val exitDialog by lazy {
        object: ExitWarningDialog(context){
            override fun ifYes(): DialogInterface.OnClickListener =
                DialogInterface.OnClickListener { dialog, _ ->
                    action(ProfileAction.Select())
                    this@SelectProfileDialog.dismiss()
                    dialog.dismiss()
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DialogProfileSelectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(false)

        binding.setupRecyclerView()
        with(binding) {
            btnCreateProfile.setOnClickListener {
                CreateProfileDialog(context){
                    val remoteState = action(ProfileAction.Create(it))!!
                    monitorState(remoteState)
                }.show()
            }
            btnClose.setOnClickListener { exitDialog.show() }
        }
    }

    private fun monitorState(state: StateFlow<RemoteState>){
        CoroutineScope(Main).launch {
            state.collect{
                when(it){
                    RemoteState.Success -> {
                        Toast.makeText(context, "Profile Created!", Toast.LENGTH_SHORT).show()
                    }
                    RemoteState.Failed -> {
                        Toast.makeText(context, "Creation Failed", Toast.LENGTH_SHORT).show()
                    }
                    RemoteState.Invalid ->
                        Toast.makeText(context, "Unexpected Error", Toast.LENGTH_SHORT).show()
                    else -> {}
                }
            }
        }
    }

    private fun DialogProfileSelectBinding.setupRecyclerView() {
        val itemsAdapter = AccountProfilesAdapter(action) { dismiss() }
        CoroutineScope(Main).launch {
            accountProfiles.collect(itemsAdapter::setList)
        }
        with(listProfiles){
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
        }
    }
}