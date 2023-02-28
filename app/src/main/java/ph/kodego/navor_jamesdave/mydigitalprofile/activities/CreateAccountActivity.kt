package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityCreateAccountBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProgressBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.Firebase
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseRegisterInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var progressDialog: Dialog
    private lateinit var formControls: FormControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        formControls = FormControls() //TODO: Validate Fields upon losing focus
        with(formControls){
            setTextValidationListener(binding.firstName, binding.tilFirstName)
            setTextValidationListener(binding.lastName, binding.tilLastName)
            setTextValidationListener(binding.password, binding.tilPassword)
            setTextValidationListener(binding.confirmPassword, binding.tilConfirmPassword)
        }
        binding.btnSignUp.setOnClickListener {
            validateForm()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateForm(){
        val firstName = binding.firstName.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        with(formControls){
            when(false){
                validateText(firstName) -> binding.firstName.requestFocus()
                validateText(lastName) -> binding.lastName.requestFocus()
                validateEmail(email) -> binding.email.requestFocus()
                validateText(password) -> binding.password.requestFocus()
                validateText(confirmPassword) -> binding.confirmPassword.requestFocus()
                validatePassword(password, confirmPassword) -> binding.password.requestFocus()
                else -> Firebase(firebaseInterface).registerUser(firstName, lastName, email, password)
            }
        }
    }

    private val firebaseInterface = object: FirebaseRegisterInterface {
        override fun showProgressDialog() {
            progressDialog = Dialog(binding.root.context)
            val progressBinding = DialogueProgressBinding.inflate(layoutInflater)
            progressBinding.progressText.setText(R.string.signing_up)
            progressDialog.setContentView(progressBinding.root)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun hideProgressDialog() {
            progressDialog.dismiss()
        }

        override fun userRegistrationFail(message: String) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }

        override fun accountRegistrationSuccess() {
            Toast.makeText(applicationContext, "Account Registration Successful", Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            finish()
        }

        override fun accountRegistrationFail() {
            Toast.makeText(applicationContext, "Account Registration Fail", Toast.LENGTH_LONG)
                .show()
        }
    }
}