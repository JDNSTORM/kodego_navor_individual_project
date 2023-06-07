package ph.kodego.navor_jamesdave.mydigitalprofile.activities.sign_in

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.CreateAccountActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ForgotPasswordActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentSignInBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseUserDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles.EXTRA_SIGNED_IN
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class SignInFragment() : ViewPagerFragment<FragmentSignInBinding>() {
    override fun getTabInformation(): TabInfo = TabInfo(
        getString(R.string.sign_in),
        R.drawable.ic_switch_account_24
    )
    private val viewModel by lazy{
        ViewModelProvider(requireActivity())[AccountViewModel::class.java]
    }
    private val progressDialog by lazy { ProgressDialog(requireContext(), R.string.signing_in) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            btnSignIn.setOnClickListener { validateForm() }
            btnSignUp.setOnClickListener { toSignUp() }
            btnForgotPassword.setOnClickListener { toForgotPassword() }
        }
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

    private fun validateForm(){
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString()
        when (false) {
            email.validEmail() -> binding.email.requestFocus()
            password.isNotEmpty() -> binding.password.requestFocus()
            else -> signIn(email, password)
        }
    }

    private fun signIn(email: String, password: String){
        progressDialog.show()
        CoroutineScope(IO).launch{
            val signInSuccessful = viewModel.signIn(email, password)
            withContext(Main) {
                if (signInSuccessful) {
                    Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
                    val activity = requireActivity()
                    val intent = activity.intent
                    intent.putExtra(EXTRA_SIGNED_IN, true)
                    progressDialog.dismiss()
                    activity.startActivity(intent)
                    activity.finish()
                } else {
                    progressDialog.dismiss()
                }
            }
        }
    }
}