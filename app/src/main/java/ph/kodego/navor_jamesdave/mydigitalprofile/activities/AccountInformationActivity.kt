package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountInformationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseAccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ProgressDialog

class AccountInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountInformationBinding
    private lateinit var account: Account
    private lateinit var progressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        progressDialog = ProgressDialog(binding.root.context, R.string.updating_account)

        if (intent.hasExtra(IntentBundles.Account)) { //TODO: Retrieve Profile
            account = intent.getParcelableExtra(IntentBundles.Account)!!
            setFormData()
        }else{
            Toast.makeText(applicationContext, "No Account Active", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnSave.setOnClickListener {
            checkFormData()
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.tbTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbTop.setNavigationOnClickListener {
//            onBackPressedDispatcher //TODO: Implement
            onBackPressed()
        }
    }
    private fun setFormData(){
        val address = account.contactInformation?.address
        val contactNumber = account.contactInformation?.contactNumber
        binding.firstName.setText(account.firstName)
        binding.lastName.setText(account.lastName)
        if (address != null){
            binding.streetAddress.setText(address.streetAddress)
            binding.subdivision.setText(address.subdivision)
            binding.city.setText(address.cityOrMunicipality)
            binding.zipCode.setText(address.zipCode.toString())
            binding.province.setText(address.province)
            binding.country.setText(address.country)
        }else{
            //TODO: Add Address
        }
        if (contactNumber != null){
            var contact = StringBuilder() //TODO
            binding.layoutContact.contactNumber.setText("(${contactNumber.areaCode}) ${contactNumber.contact}")
        }
    }
    private fun checkFormData(){
        val updatedAccount = Account(account)
        with(updatedAccount){
            firstName = binding.firstName.text.toString()
            lastName = binding.lastName.text.toString()
        }
        val updatedAccountData = FormControls().getModified(account, updatedAccount)
        if (updatedAccountData.size > 0){
            progressDialog.show()
            lifecycleScope.launch {
                if (FirebaseAccountDAOImpl(binding.root.context).updateAccount(updatedAccountData)){
                    account.setAccount(updatedAccount)
                    progressDialog.dismiss()
                    finish()
                }else{
                    Toast.makeText(applicationContext, "Update Failed", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
        }
    }
}