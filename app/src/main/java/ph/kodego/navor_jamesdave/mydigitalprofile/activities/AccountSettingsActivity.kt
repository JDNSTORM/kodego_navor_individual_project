package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.MainActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountSettingsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseUserDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog

@Deprecated("Replaced with Activity from activities/account")
class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountSettingsBinding
    private lateinit var dao: FirebaseUserDAOImpl
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        dao = FirebaseUserDAOImpl(applicationContext)
        progressDialog = ProgressDialog(binding.root.context, R.string.updating_password)

        binding.btnSave.setOnClickListener { validateForm() }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.tbTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbTop.setNavigationOnClickListener {
//            onBackPressedDispatcher //TODO: Implement
            onBackPressed()
        }
    }
    private fun validateForm(){
        val oldPassword = binding.oldPassword.text.toString()
        val password = binding.newPassword.text.toString()
        val confirmPassword = binding.confirmPassword.text.toString()

        when(false){
            oldPassword.isNotEmpty() -> binding.oldPassword.requestFocus()
            password.isNotEmpty() -> binding.newPassword.requestFocus()
            confirmPassword.isNotEmpty() -> binding.confirmPassword.requestFocus()
            (password == confirmPassword) -> binding.newPassword.requestFocus()
            else -> updatePassword(oldPassword, password)
        }
    }
    private fun updatePassword(oldPassword: String, password: String){
        progressDialog.show()
        lifecycleScope.launch{
            if(dao.updateUserPassword(oldPassword, password)){
                dao.signOutUser()
                progressDialog.dismiss()
                goToMain()
            }else{
                progressDialog.dismiss()
            }
        }
    }

    private fun goToMain(){
        val intent = Intent(baseContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
//        super.onBackPressed()
        goToMain()
    }
}