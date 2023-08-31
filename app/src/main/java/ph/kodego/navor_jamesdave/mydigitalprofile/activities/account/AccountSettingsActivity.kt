package ph.kodego.navor_jamesdave.mydigitalprofile.activities.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountSettingsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class AccountSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: AccountViewModel by viewModels()
        binding.setupUI(
            viewModel.accountState,
            viewModel.action
        )
    }

    private fun ActivityAccountSettingsBinding.setupUI(
        state: StateFlow<AccountState>,
        action: (AccountAction) -> StateFlow<RemoteState>?
    ) {
        setupActionBar()
        monitorState(state)
        btnSave.setOnClickListener { validateForm{ oldPassword, password ->
            action(AccountAction.ChangePassword(oldPassword, password))
        } }
    }

    private fun monitorState(state: StateFlow<AccountState>) {
        val progressDialog = ProgressDialog(this, R.string.updating_password).create()
        lifecycleScope.launch {
            state.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{
                when(it){
                    is AccountState.Active -> progressDialog.dismiss()
                    is AccountState.Updating -> progressDialog.show()
                    is AccountState.Error -> showError(it.error)
                    else -> signIn()
                }
            }
        }
    }

    private fun signIn() {
        Toast.makeText(this, "Password Updated! Sign In again", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun showError(t: Throwable){
        Toast.makeText(this, t.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    private fun ActivityAccountSettingsBinding.validateForm(
        updatePassword: (String, String) -> Unit
    ) {
        val oldPasswordText = oldPassword.text.toString()
        val passwordText = newPassword.text.toString()
        val confirmPasswordText = confirmPassword.text.toString()

        when(false){
            oldPasswordText.isNotEmpty() -> oldPassword.requestFocus()
            passwordText.isNotEmpty() -> newPassword.requestFocus()
            confirmPasswordText.isNotEmpty() -> confirmPassword.requestFocus()
            (passwordText == confirmPasswordText) -> newPassword.requestFocus()
            else -> updatePassword(oldPasswordText, passwordText)
        }
    }

    private fun ActivityAccountSettingsBinding.setupActionBar(){
        setSupportActionBar(tbTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tbTop.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}