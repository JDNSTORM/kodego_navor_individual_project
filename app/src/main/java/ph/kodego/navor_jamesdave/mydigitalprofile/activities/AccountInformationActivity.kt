package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountInformationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseAccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseContactInformationDAO
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseContactInformationDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ProgressDialog

class AccountInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountInformationBinding
    private lateinit var account: Account
    private lateinit var progressDialog: Dialog
    private lateinit var accountDAO: FirebaseAccountDAOImpl
    private lateinit var contactInformationDAO: FirebaseContactInformationDAOImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        accountDAO = FirebaseAccountDAOImpl(applicationContext)
        contactInformationDAO = FirebaseContactInformationDAOImpl()

        if (intent.hasExtra(IntentBundles.Account)) {
            account = intent.getParcelableExtra(IntentBundles.Account)!!
            setFormData()
        }else{
            Toast.makeText(applicationContext, "No Account Active", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupActionBar()
        progressDialog = ProgressDialog(binding.root.context, R.string.updating_account)

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
        val address = account.contactInformation!!.address
        val contactNumber = account.contactInformation!!.contactNumber
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
            account.contactInformation!!.address = Address(account.contactInformationID)
            lifecycleScope.launch {
                contactInformationDAO.addAddress(account.contactInformation!!.address!!)
            }
        }
        if (contactNumber != null){
            var contact = StringBuilder() //TODO: Format Contact Number
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
                if (accountDAO.updateAccount(updatedAccountData)){
                    account.setAccount(updatedAccount) //TODO: ActivityForResult
                    Toast.makeText(applicationContext, "Account Updated", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                    finish()
                }else{
                    Snackbar.make(binding.root, "Update Failed", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
        }else{
            Snackbar.make(binding.root, "No Fields Changed", Toast.LENGTH_SHORT).show()
        }
    }
}