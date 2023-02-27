package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityCreateAccountBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProgressBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FireStore
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account

class CreateAccountActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        binding.btnSignUp.setOnClickListener {
            validateForm()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateText(text: String): Boolean{
        return text.isNotEmpty()
    }
    private fun validateEmail(email: String): Boolean{
        val emailRegex: Regex = Regex("^[A-Za-z](.*)([@]{1})(.+)(\\.)(.+)")
        return email.matches(emailRegex)
    }
    private fun validatePassword(password: String, confirmPassword: String): Boolean{
        return password == confirmPassword
    }

    private fun validateForm(){
        val firstName = binding.firstName.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        when(false){
            validateText(firstName) -> binding.firstName.requestFocus()
            validateText(lastName) -> binding.lastName.requestFocus()
            validateEmail(email) -> binding.email.requestFocus()
            validateText(password) -> binding.password.requestFocus()
            validateText(confirmPassword) -> binding.confirmPassword.requestFocus()
            validatePassword(password, confirmPassword) -> binding.password.requestFocus()
            else -> registerUser(firstName, lastName, email, password)
        }
    }

    private fun registerUser(firstName: String, lastName: String, email: String, password: String){
        showProgressDialog()
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val firebaseUser: FirebaseUser = task.result!!.user!!
                val registeredEmail = firebaseUser.email!!
                val account = Account(firebaseUser.uid, firstName, lastName, email)
                if (FireStore().registerAccount(account)) { //TODO: Get After Task is Completed
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Registration Successful", Toast.LENGTH_SHORT).show()
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }else{
                progressDialog.dismiss()
                Toast.makeText(applicationContext, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProgressDialog(){
        progressDialog = Dialog(binding.root.context)
        val progressBinding = DialogueProgressBinding.inflate(layoutInflater)
        progressDialog.setContentView(progressBinding.root)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }
}