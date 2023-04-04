package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.app.Dialog
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activity_results_contracts.OpenDocumentContract
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountInformationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseAccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseContactInformationDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseStorageDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ProgressDialog
// TODO: Upload Image
class AccountInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountInformationBinding
    private lateinit var account: Account
    private lateinit var progressDialog: Dialog
    private lateinit var accountDAO: FirebaseAccountDAOImpl
    private lateinit var contactInformationDAO: FirebaseContactInformationDAOImpl

    private val openDocumentLauncher = registerForActivityResult(OpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) } ?: Log.e("Document", "Image Pick Cancelled")
    }

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

        binding.profilePicture.setOnClickListener { openDocumentLauncher.launch(arrayOf("image/*")) }

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
        Log.d("Address", address.toString())
        Log.d("Email", account.contactInformation!!.emailAddress.toString())
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
            binding.layoutContactEdit.telAreaCode.setText(contactNumber.areaCode)
            binding.layoutContactEdit.telContactNumber.setText("${contactNumber.contact}")
        }else{
            account.contactInformation!!.contactNumber = ContactNumber(account.contactInformationID)
            lifecycleScope.launch {
                contactInformationDAO.addContactNumber(account.contactInformation!!.contactNumber!!)
            }
        }
    }
    private fun checkFormData(){
        val updatedAccount = Account(account)
        val updatedAddress = Address(updatedAccount.contactInformation!!.address!!)
        val updatedContactNumber = ContactNumber(updatedAccount.contactInformation!!.contactNumber!!)
        with(updatedAccount){
            firstName = binding.firstName.text.toString()
            lastName = binding.lastName.text.toString()
        }
        with(updatedContactNumber){
            areaCode = binding.layoutContactEdit.telAreaCode.text.toString()
            contact = binding.layoutContactEdit.telContactNumber.text.toString().toLong()
        }
        with(updatedAddress){
            streetAddress = binding.streetAddress.text.toString()
            subdivision = binding.subdivision.text.toString()
            cityOrMunicipality = binding.city.text.toString()
            zipCode = binding.zipCode.text.toString().toInt()
            province = binding.province.text.toString()
            country = binding.country.text.toString()
        }
        val updatedAccountData = FormControls().getModified(account, updatedAccount)
        val updatedAddressData = FormControls().getModified(account.contactInformation!!.address!!, updatedAddress)
        val updatedContactNumberData = FormControls().getModified(account.contactInformation!!.contactNumber!!, updatedContactNumber)
        if (updatedAccountData.size > 0 || updatedAddressData.size > 0 || updatedContactNumberData.size > 0){
            progressDialog.show()
            lifecycleScope.launch {
                if (
                    accountDAO.updateAccount(updatedAccountData) &&
                    contactInformationDAO.updateAddress(updatedAddress, updatedAddressData) &&
                    contactInformationDAO.updateContactNumber(updatedContactNumber, updatedContactNumberData)
                ){
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

    private fun onImageSelected(uri: Uri){
        Log.i("URI", uri.toString())
//        progressDialog.show()
        lifecycleScope.launch {
            val photoURL = FirebaseStorageDAOImpl(applicationContext).updateAccountPhoto(uri)
            if (photoURL.isNotEmpty() && photoURL != "null"){
                val updateImageMap = HashMap<String, Any?>()
                updateImageMap["image"] = photoURL
                if(accountDAO.updateAccount(updateImageMap)){
                    Glide
                        .with(binding.root.context)
                        .load(photoURL)
                        .circleCrop()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(binding.profilePicture)
                }
            }
        }
    }
}