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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.MigrateActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.dialogs.MigrateDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentAccountBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class AccountFragment(): ViewPagerFragment<FragmentAccountBinding>(), FlowCollector<Account?> {
    override fun getTabInformation(): TabInfo = TabInfo("Account", R.drawable.ic_account_circle_24)
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readAccount()

        with(binding) {
            btnAccountInformation.setOnClickListener { toAccountInformation() }
            btnAccountSettings.setOnClickListener { toAccountSettings() }
            btnViewProfile.setOnClickListener { toProfile() }
            btnSignOut.setOnClickListener { signOut() }
        }
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

    private fun readAccount(){
//        progressDialog.show() //TODO: Change to LayoutLoading
        lifecycleScope.launch {
            viewModel.activeAccount.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect(this@AccountFragment)
        }
    }

    override suspend fun emit(value: Account?) {
        value?.let {
            setAccountData(it)
//            progressDialog.dismiss()
        } ?: object: MigrateDialog(requireContext()){
            override fun ifYes(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                toMigrate()
            }

            override fun ifNo(): DialogInterface.OnClickListener = DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                signOut()
            }
        }.show()
    }

    private fun toMigrate() {
        val intent = Intent(requireContext(), MigrateActivity::class.java)
        startActivity(intent)
    }

    private fun setAccountData(account: Account){
        with(binding) {
            profileUserName.text = account.displayName()
            email.text = account.emailAddress
            GlideModule().loadProfilePhoto(binding.profilePicture, account.image)
        }
    }

    private fun signOut(){
        viewModel.signOut()
        val activity = requireActivity()
        val intent = activity.intent
        activity.finish()
        startActivity(intent)
    }

    override fun onResume() {
        FirebaseAuth.getInstance().currentUser ?: signOut()
        super.onResume()
    }
}