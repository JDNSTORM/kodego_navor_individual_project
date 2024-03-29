package ph.kodego.navor_jamesdave.mydigitalprofile.activities.account

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.deprecated.MigrateActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.MigrateDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentAccountBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadProfile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class AccountFragment(): ViewPagerFragment<FragmentAccountBinding>() {
    override fun getTabInformation(): TabInfo = TabInfo("Account", R.drawable.ic_account_circle_24)
    private val viewModel: AccountViewModel by viewModels()

    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentAccountBinding {
        return FragmentAccountBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setupUI(
            viewModel.accountState
        ) {
            viewModel.action(AccountAction.SignOut)
        }
    }

    private fun FragmentAccountBinding.setupUI(
        state: StateFlow<AccountState>,
        signOut: () -> Unit
    ) {
        lifecycleScope.launch {
            state.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{ accountState ->
                when(accountState){
                    is AccountState.Active -> launch{
                        accountState.account.collect { bind(it) }
                    }
                    is AccountState.Error -> reloadActivity()
                    is AccountState.Inactive -> reloadActivity()
                    else -> {}
                }
            }
        }

        btnAccountInformation.setOnClickListener { toAccountInformation() }
        btnAccountSettings.setOnClickListener { toAccountSettings() }
        btnViewProfile.setOnClickListener { toProfile() }
        btnSignOut.setOnClickListener { signOut() }
    }

    private fun FragmentAccountBinding.bind(account: Account?){
        account?.let {
            profileUserName.text = it.displayName()
            email.text = it.emailAddress
            profilePicture.loadProfile(it.image)
        }?: object: MigrateDialog(requireContext()){
            override fun ifYes(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                toMigrate()
            }

            override fun ifNo(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                Firebase.auth.signOut()
                reloadActivity()
            }
        }.show()
    }

    private fun toProfile() {
        val intent = Intent(requireContext(), ProfileActivity::class.java)
        startActivity(intent)
    }

    private fun toAccountSettings() {
        val intent = Intent(requireContext(), AccountSettingsActivity::class.java)
        startActivity(intent)
    }

    private fun toAccountInformation() {
        val intent = Intent(requireContext(), AccountInformationActivity::class.java)
        startActivity(intent)
    }

    private fun toMigrate() {
        val intent = Intent(requireContext(), MigrateActivity::class.java)
        requireActivity().finish()
        startActivity(intent)
    }

    private fun reloadActivity(){
        val activity = requireActivity()
        val intent = activity.intent
        activity.finish()
        startActivity(intent)
    }

    override fun onResume() {
        FirebaseAuth.getInstance().currentUser ?: reloadActivity()
        super.onResume()
    }
}