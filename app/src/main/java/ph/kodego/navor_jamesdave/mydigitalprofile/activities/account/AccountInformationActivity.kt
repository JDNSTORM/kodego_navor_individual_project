package ph.kodego.navor_jamesdave.mydigitalprofile.activities.account

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountInformationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseStorageDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_ADDRESS
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_CONTACT_NUMBER
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Address
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.ContactNumber
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class AccountInformationActivity(): AppCompatActivity() {
    private val binding by lazy { ActivityAccountInformationBinding.inflate(layoutInflater) }
    private val viewModel: AccountViewModel by viewModels()
    private val progressDialog by lazy { ProgressDialog(this, R.string.updating_account) }

    private val pictureChosen = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if(result.resultCode == Activity.RESULT_CANCELED){
            Log.e("Document", "Image Pick Cancelled")
        }else{
            val data = result.data
            if(data == null){
                Toast.makeText(applicationContext, "No image was chosen", Toast.LENGTH_SHORT).show()
            }else{
                val imageFileUri = result.data!!.data!!
                onImageSelected(imageFileUri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
//        setContentView(binding.root)
        setupActionBar()
        loadAccount()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.tbTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbTop.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun loadAccount(){
        lifecycleScope.launch {
            val account = viewModel.readActiveAccount().single()
            account?.let { setFormData(it) } ?: accountLoadFailed()
        }
    }

    private fun setFormData(account: Account){
        val missingFields: HashMap<String, Any?> = HashMap()
        with(binding){
            firstName.setText(account.firstName)
            lastName.setText(account.lastName)
            if (account.image.isNotEmpty()){
                GlideModule().loadProfilePhoto(profilePicture, account.image)
            }
            account.address?.let {address ->
                streetAddress.setText(address.streetAddress)
                subdivision.setText(address.subdivision)
                city.setText(address.cityOrMunicipality)
                zipCode.setText(address.zipCode.toString())
                province.setText(address.province)
                country.setText(address.country)
            } ?: run{ missingFields[KEY_ADDRESS] = Address() }
            account.contactNumber?.let {contactNumber ->
                layoutContactEdit.telAreaCode.setText(contactNumber.areaCode)
                layoutContactEdit.telContactNumber.setText("${contactNumber.contact}")
            } ?: run{ missingFields[KEY_CONTACT_NUMBER] = ContactNumber() }
        }
        if (missingFields.isNotEmpty()){
            CoroutineScope(IO).launch { viewModel.updateAccount(missingFields) }
        }
    }

    private fun accountLoadFailed(){
        Toast.makeText(this, "Error Loading Account", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun onImageSelected(uri: Uri){
        //TODO
    }
}