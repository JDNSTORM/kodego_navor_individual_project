package ph.kodego.navor_jamesdave.mydigitalprofile.activities.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentAccountBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class AccountFragment(): ViewPagerFragment<FragmentAccountBinding>(), FlowCollector<Account?> {
    override fun getTabInformation(): TabInfo = TabInfo("Account", R.drawable.ic_account_circle_24)
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }
    private val progressDialog by lazy { ProgressDialog(requireContext(), R.string.loading_account) }

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
        binding.btnAccountInformation.setOnClickListener { toAccountInformation() }
        binding.btnSignOut.setOnClickListener { signOut() }
    }

    private fun toAccountInformation() {
//        val intent = Intent(requireContext(), AccountInformationActivity::class.java)
//        startActivity(intent)
    }

    private fun readAccount(){
        progressDialog.show()
        lifecycleScope.launch {
            viewModel.readActiveAccount().collect(this@AccountFragment)
        }
    }

    override suspend fun emit(value: Account?) {
        value?.let {
            setAccountData(it)
            progressDialog.dismiss()
        } ?: run{
            Toast.makeText(context, "Failed to get Account Data", Toast.LENGTH_LONG).show()
            progressDialog.dismiss()
            signOut()
        }
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
}