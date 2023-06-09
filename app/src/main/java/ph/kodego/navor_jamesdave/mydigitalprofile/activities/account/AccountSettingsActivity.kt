package ph.kodego.navor_jamesdave.mydigitalprofile.activities.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountSettingsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class AccountSettingsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAccountSettingsBinding.inflate(layoutInflater) }
    private val viewModel: AccountViewModel by viewModels()
    private val progressDialog by lazy { ProgressDialog(this, R.string.updating_password) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()

        binding.btnSave.setOnClickListener { validateForm() }
    }

    private fun validateForm() {
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

    private fun updatePassword(oldPassword: String, password: String) {
        progressDialog.show()
        CoroutineScope(IO).launch {
            val updateSuccessful = viewModel.updateUserPassword(oldPassword, password)
            withContext(Main){
                if (updateSuccessful){
                    finish()
                }else{

                }
                progressDialog.dismiss()
            }
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.tbTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbTop.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}