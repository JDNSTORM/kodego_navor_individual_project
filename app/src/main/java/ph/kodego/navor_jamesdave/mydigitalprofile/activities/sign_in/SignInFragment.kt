package ph.kodego.navor_jamesdave.mydigitalprofile.activities.sign_in

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ForgotPasswordActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentSignInBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles.EXTRA_SIGNED_IN
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class SignInFragment() : ViewPagerFragment<FragmentSignInBinding>() {
    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSignInBinding {
        return FragmentSignInBinding.inflate(inflater, container, false)
    }

    override fun getTabInformation(): TabInfo = TabInfo(
        "Sign In",
        R.drawable.ic_switch_account_24
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: AccountViewModel by viewModels()
        binding.setupUI(
            viewModel.accountState
        ) { email, password ->
            viewModel.action(AccountAction.SignIn(email, password))
        }
    }

    private fun FragmentSignInBinding.setupUI(
        accountState: StateFlow<AccountState>,
        signIn: (String, String) -> Unit
    ) {
        val progressDialog = ProgressDialog(requireContext(), R.string.signing_in)
        lifecycleScope.launch {
            accountState.collectLatest{
                when(it){
                    is AccountState.Active -> {
                        progressDialog.dismiss()
                        reloadActivity()
                    }
                    is AccountState.Updating -> progressDialog.show()
                    is AccountState.Error -> showError(it.error)
                    else -> {}
                }
            }
        }

        btnSignIn.setOnClickListener { validateForm(signIn) }
        btnSignUp.setOnClickListener { toSignUp() }
        btnForgotPassword.setOnClickListener { toForgotPassword() }
    }

    private fun FragmentSignInBinding.showError(t: Throwable){
        Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun toForgotPassword() {
        val intent = Intent(context, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun toSignUp() {
        val intent = Intent(context, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun String.validEmail(): Boolean{
        val emailRegex = Regex("^[A-Za-z](.*)(@+)(.+)(\\.)(.+)")
        return matches(emailRegex)
    }

    private fun FragmentSignInBinding.validateForm(signIn: (String, String) -> Unit) {
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString()
        when (false) {
            emailText.validEmail() -> email.requestFocus()
            passwordText.isNotEmpty() -> password.requestFocus()
            else -> signIn(emailText, passwordText)
        }
    }

//    private fun signIn(email: String, password: String){
//        progressDialog.show()
//        CoroutineScope(IO).launch{
//            val signInSuccessful = viewModel.signIn(email, password)
//            withContext(Main) {
//                if (signInSuccessful) {
//                    Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
//                    val activity = requireActivity()
//                    val intent = activity.intent
//                    intent.putExtra(EXTRA_SIGNED_IN, true)
//                    progressDialog.dismiss()
//                    activity.finish()
//                    startActivity(intent)
//                } else {
//                    progressDialog.dismiss()
//                }
//            }
//        }
//    }

    private fun reloadActivity(){
        Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
        val activity = requireActivity()
        val intent = activity.intent
        intent.putExtra(EXTRA_SIGNED_IN, true)
        activity.finish()
        startActivity(intent)
    }
}