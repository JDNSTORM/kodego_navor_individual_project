package ph.kodego.navor_jamesdave.mydigitalprofile.activities.sign_in

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivitySignUpBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    private val binding by lazy {ActivitySignUpBinding.inflate(layoutInflater)}
    private val viewModel: AccountViewModel by viewModels()
    private val progressDialog by lazy { ProgressDialog(this, R.string.signing_up) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()

        binding.btnSignUp.setOnClickListener { validateForm() }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun validateForm(){
        var valid = validateRequired(
            binding.confirmPassword,
            binding.password,
            binding.email,
            binding.lastName,
            binding.firstName
        )
        if (!valid) return

        val firstName = binding.firstName.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        if (password != confirmPassword){
            binding.password.error = "Passwords do not match."
            binding.confirmPassword.error = "Passwords do not match."
            binding.password.requestFocus()
            valid = false
        }

        if (!email.validEmail()) {
            binding.email.error = "Not a valid email"
            binding.email.requestFocus()
            valid = false
        }

        if (valid) registerAccount(firstName, lastName, email, password)
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

    private fun registerAccount(firstName: String, lastName: String, email: String, password: String){
//        progressDialog.show()
//        lifecycleScope.launch {
//            val signUpSuccessful = viewModel.signUp(firstName, lastName, email, password)
//            withContext(Main) {
//                if (signUpSuccessful) {
//                    viewModel.signOut()
//                    Toast.makeText(applicationContext, "Registration Successful!", Toast.LENGTH_SHORT)
//                        .show()
//                    finish()
//                }
//                progressDialog.dismiss()
//            }
//        }
    }

    private fun String.validEmail(): Boolean{
        val emailRegex = Regex("^[A-Za-z](.*)(@+)(.+)(\\.)(.+)")
        return matches(emailRegex)
    }
}