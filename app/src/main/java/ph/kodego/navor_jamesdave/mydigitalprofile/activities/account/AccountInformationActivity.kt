package ph.kodego.navor_jamesdave.mydigitalprofile.activities.account

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountInformationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_ADDRESS
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_CONTACT_NUMBER
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_FIRST_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_IMAGE
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_LAST_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class AccountInformationActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAccountInformationBinding.inflate(layoutInflater) }
    private val viewModel: AccountViewModel by viewModels()
    private val progressDialog by lazy { ProgressDialog(this, R.string.updating_account) }
    private lateinit var account: Account
    private var pickedImage: Uri? = null

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if(result.resultCode == Activity.RESULT_CANCELED){
            Log.e("Document", "Image Pick Cancelled")
        }else{
            val data = result.data
            data?.let {
                pickedImage = data.data
                onImageSelected()
            } ?: kotlin.run {
                Toast.makeText(applicationContext, "No image was chosen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()
        loadAccount()

        with(binding){
            profilePicture.setOnClickListener { chooseProfilePicture() }
            btnSave.setOnClickListener { getFormData() }
        }
    }

    private fun getFormData() {
        val newAccount: Account
        with(binding) {
             newAccount = Account(
                account,
                firstName.text.toString().trim(),
                lastName.text.toString().trim()
            )
            newAccount.address.let {
                it.streetAddress = streetAddress.text.toString().trim()
                it.subdivision = binding.subdivision.text.toString().trim()
                it.cityOrMunicipality = binding.city.text.toString().trim()
                it.zipCode = binding.zipCode.text.toString().toIntOrNull() ?: 0
                it.province = binding.province.text.toString().trim()
                it.country = binding.country.text.toString().trim()
            }
            newAccount.contactNumber.let {
                it.areaCode = layoutContactEdit.telAreaCode.text.toString()
                it.contact = layoutContactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0
            }
        }
        checkChanges(newAccount)
    }

    private fun checkChanges(newAccount: Account) {
        val changes: HashMap<String, Any?> = HashMap()
        if (account.firstName != newAccount.firstName) changes[KEY_FIRST_NAME] = newAccount.firstName
        if (account.lastName != newAccount.lastName) changes[KEY_LAST_NAME] = newAccount.lastName
        if (account.address != newAccount.address) changes[KEY_ADDRESS] = newAccount.address
        if (account.contactNumber != newAccount.contactNumber) changes[KEY_CONTACT_NUMBER] = newAccount.contactNumber
        pickedImage?.let { changes[KEY_IMAGE] = "" }

        if (changes.isNotEmpty()) updateAccount(changes)
    }

    private fun updateAccount(changes: HashMap<String, Any?>){
        progressDialog.show()
        CoroutineScope(IO).launch {
            pickedImage?.let {
                viewModel.uploadImage(it)?.let {
                    changes[KEY_IMAGE] = it
                } ?: changes.remove(KEY_IMAGE)
            }

            val updateSuccessful = viewModel.updateAccount(changes)
            withContext(Main){
                if(updateSuccessful){
                    Toast.makeText(
                        this@AccountInformationActivity,
                        "Account Updated!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }else{
                    Toast.makeText(
                        this@AccountInformationActivity,
                        "Updated Failed",
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun loadAccount(){
        lifecycleScope.launch {
            val activeAccount = viewModel.readActiveAccount().first()
            activeAccount?.let {
                account = it
                setFormData()
            } ?: accountLoadFailed()
        }
    }

    private fun setFormData(){
        with(binding){
            firstName.setText(account.firstName)
            lastName.setText(account.lastName)
            if (account.image.isNotEmpty()){
                GlideModule().loadProfilePhoto(profilePicture, account.image)
            }
            account.address.let {address ->
                streetAddress.setText(address.streetAddress)
                subdivision.setText(address.subdivision)
                city.setText(address.cityOrMunicipality)
                zipCode.setText(address.zipCode.toString())
                province.setText(address.province)
                country.setText(address.country)
            }
            account.contactNumber.let {contactNumber ->
                layoutContactEdit.telAreaCode.setText(contactNumber.areaCode)
                layoutContactEdit.telContactNumber.setText("${contactNumber.contact}")
            }
        }
    }

    private fun accountLoadFailed(){
        Toast.makeText(this, "Error Loading Account", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun onImageSelected(){
        pickedImage?.let {
            GlideModule().loadProfilePhoto(binding.profilePicture, it.toString())
        }
    }

    private fun chooseProfilePicture(){
        val choosePictureIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        launcher.launch(choosePictureIntent)

//        when (Build.VERSION.SDK_INT){
//            TIRAMISU -> {
//                if (ActivityCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PERMISSION_GRANTED) {
//                    val choosePictureIntent = Intent(
//                        ACTION_PICK,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                    )
//                    pictureChosen.launch(choosePictureIntent)
//                }else{
//                    requestMediaImagesPermission()
//                }
//            }
//            else -> {
//                openDocumentLauncher.launch(arrayOf("image/*"))
//            }
//        }
    }
}