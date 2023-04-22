package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityCreateAccountBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseAccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var progressDialog: Dialog
    private lateinit var formControls: FormControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        progressDialog = ProgressDialog(binding.root.context, R.string.signing_up)

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

        when(false){
            firstName.isNotEmpty() -> binding.firstName.requestFocus()
            lastName.isNotEmpty() -> binding.lastName.requestFocus()
            email.isNotEmpty() -> binding.email.requestFocus()
            password.isNotEmpty() -> binding.password.requestFocus()
            confirmPassword.isNotEmpty() -> binding.confirmPassword.requestFocus()
            (password == confirmPassword) -> binding.password.requestFocus()
            else -> registerAccount(firstName, lastName, email, password)
        }
    }

    private fun registerAccount(firstName: String, lastName: String, email: String, password: String){
        progressDialog.show()
        lifecycleScope.launch {
            val dao = FirebaseAccountDAOImpl(applicationContext)
            if (dao.registerAccount(firstName, lastName, email, password)) {
                dao.signOutUser()
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Account Registered", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                progressDialog.dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in.
        if (Firebase.auth.currentUser != null) {
            // Not signed in, launch the Sign In activity
            finish()
            Toast.makeText(applicationContext, "A user is signed in", Toast.LENGTH_SHORT).show()
            return
        }
    }
}