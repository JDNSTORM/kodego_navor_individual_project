package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile

import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.ExitWarningDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ProfileAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.AccountProfilesAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogProfileSelectBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile

class SelectProfileDialog(
    context: Context,
    private val accountProfiles: Flow<List<Profile>>,
    private val action: (ProfileAction) -> StateFlow<RemoteState>?,
): MaterialAlertDialogBuilder(context){
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogProfileSelectBinding

    override fun create(): AlertDialog {
        binding = DialogProfileSelectBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        setCancelable(false)
        return super.create().also {
            dialog = it
            binding.setupRecyclerView()
            with(binding) {
                btnCreateProfile.setOnClickListener {
                    CreateProfileDialog(context) {
                        val remoteState = action(ProfileAction.Create(it))!!
                        monitorState(remoteState)
                    }.show()
                }
                btnClose.setOnClickListener { exitDialog.show() }
            }
        }
    }

    private val exitDialog by lazy {
        ExitWarningDialog(context){
            action(ProfileAction.Select())
            dialog.dismiss()
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
        val itemsAdapter = AccountProfilesAdapter(action, dialog::dismiss)
        CoroutineScope(Main).launch {
            accountProfiles.collect(itemsAdapter::setList)
        }
        with(listProfiles){
            layoutManager = LinearLayoutManager(context)
            adapter = itemsAdapter
        }
    }
}