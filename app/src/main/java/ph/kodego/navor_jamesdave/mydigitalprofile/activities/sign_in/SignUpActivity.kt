package ph.kodego.navor_jamesdave.mydigitalprofile.activities.sign_in

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivitySignUpBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: AccountViewModel by viewModels()
        binding.setupUI(
            viewModel.accountState,
            viewModel.action
        )
    }

    private fun ActivitySignUpBinding.setupUI(
        state: StateFlow<AccountState>,
        action: (AccountAction) -> StateFlow<RemoteState>?
    ) {
        setupActionBar()
        monitorState(state){action(AccountAction.SignOut)}

        btnSignUp.setOnClickListener { validateForm{ firstName, lastName, email, password ->
            action(AccountAction.SignUp(firstName, lastName, email, password))
        } }
    }

    private fun monitorState(
        state: StateFlow<AccountState>,
        signOut: () -> Unit
    ) {
        val progressDialog = ProgressDialog(this@SignUpActivity, R.string.signing_up)
        lifecycleScope.launch {
            state.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{
                Log.d("AccountState", it.toString())
                when(it){
                    is AccountState.Active -> {
                        progressDialog.dismiss()
                        signUpSuccessful { signOut() }
                    }
                    is AccountState.Updating -> progressDialog.show()
                    is AccountState.Error -> showError(it.error)
                    else -> progressDialog.dismiss()
                }
            }
        }
    }

    private fun signUpSuccessful(signOut: () -> Unit){
        signOut()
        Toast.makeText(applicationContext, "Registration Successful!", Toast.LENGTH_SHORT)
            .show()
        finish()
    }

    private fun showError(t: Throwable){
        Toast.makeText(this, t.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun ActivitySignUpBinding.setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun ActivitySignUpBinding.validateForm(
        register: (String, String, String, String) -> Unit
    ){
        var valid = validateRequired(
            confirmPassword,
            password,
            email,
            lastName,
            firstName
        )
        if (!valid) return

        val firstNameText = firstName.text.toString().trim()
        val lastNameText = lastName.text.toString().trim()
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString()
        val confirmPasswordText = confirmPassword.text.toString()

        if (passwordText != confirmPasswordText){
            password.error = "Passwords do not match."
            confirmPassword.error = "Passwords do not match."
            password.requestFocus()
            valid = false
        }

        if (!emailText.validEmail()) {
            email.error = "Not a valid email"
            email.requestFocus()
            valid = false
        }

        if (valid) register(firstNameText, lastNameText, emailText, passwordText)
    }

    private fun validateRequired(vararg views: EditText): Boolean {
        var valid = true
        views.forEach {
            val emptyFieldError = "Field is Required"
            if (it.text.toString().trim().isEmpty()){
                it.error = emptyFieldError
                it.requestFocus()
                valid = false
            }
        }
        return valid
    }

    private fun String.validEmail(): Boolean{
        val emailRegex = Regex("^[A-Za-z](.*)(@+)(.+)(\\.)(.+)")
        return matches(emailRegex)
    }
}