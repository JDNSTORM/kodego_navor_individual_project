package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career.CreateProfileDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.ExitWarningDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ProfileAction
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.AccountProfilesAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogProfileSelectBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

class SelectProfileDialog(
    context: Context,
    private val accountProfiles: Flow<List<Profile>>,
    private val action: (ProfileAction) -> Unit,
): AlertDialog(context){
    private val binding by lazy { DialogProfileSelectBinding.inflate(layoutInflater) }
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
        setContentView(binding.root)
        setCancelable(false)

        binding.setupRecyclerView()
        with(binding) {
            btnCreateProfile.setOnClickListener {
                CreateProfileDialog(context){
                    action(ProfileAction.Create(it))
                }.show()
            }
            btnClose.setOnClickListener { exitDialog.show() }
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