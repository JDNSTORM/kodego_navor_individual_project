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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityAccountInformationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadProfile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_ADDRESS
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_CONTACT_NUMBER
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_FIRST_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_IMAGE
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Account.Companion.KEY_LAST_NAME
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class AccountInformationActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAccountInformationBinding.inflate(layoutInflater) }
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
                binding.loadImage()
            } ?: kotlin.run {
                Toast.makeText(applicationContext, "No image was chosen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val viewModel: AccountViewModel by viewModels()

        binding.setupUI(
            viewModel.accountState,
            viewModel.action
        )
    }

    private fun ActivityAccountInformationBinding.setupUI(
        state: StateFlow<AccountState>,
        action: (AccountAction) -> StateFlow<RemoteState>?
    ) {
        setupActionBar()

        lifecycleScope.launch {
            val accountFlow = (state.value as? AccountState.Active)?.account
            accountFlow?.let { flow ->
                flow.firstOrNull()?.let {
                    account = it
                    setFormData()
                }
            } ?: unauthorizedAccess()
        }

        profilePicture.setOnClickListener { chooseProfilePicture() }
        btnSave.setOnClickListener { getFormData{
            val remoteState = action(AccountAction.Update(it, pickedImage))!!
            monitorUpdate(remoteState)
        } }
    }

    private fun monitorUpdate(state: StateFlow<RemoteState>) {
        val progressDialog = ProgressDialog(this@AccountInformationActivity, R.string.updating_account)
        lifecycleScope.launch {
            state.collect{
                when(it){
                    RemoteState.Waiting -> progressDialog.show()
                    RemoteState.Success -> {
                        progressDialog.dismiss()
                        updateSuccessful()
                    }
                    RemoteState.Failed -> {
                        updateFailed()
                        progressDialog.dismiss()
                    }
                    else -> {
                        progressDialog.dismiss()
                        unauthorizedAccess()
                    }
                }
            }
        }
    }

    private fun updateSuccessful(){
        Toast.makeText(this, "Account Updated!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun updateFailed(){
        Toast.makeText(this, "Updated Failed", Toast.LENGTH_SHORT).show()
    }

    private fun unauthorizedAccess(){
        Toast.makeText(this, "Unauthorized Access!!!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun ActivityAccountInformationBinding.getFormData(update: (Map<String, Any?>) -> Unit) {
        val newAccount = Account(
            account,
            firstName.text.toString().trim(),
            lastName.text.toString().trim()
        )
        newAccount.address.let {
            it.streetAddress = streetAddress.text.toString().trim()
            it.subdivision = subdivision.text.toString().trim()
            it.cityOrMunicipality = city.text.toString().trim()
            it.zipCode = zipCode.text.toString().toIntOrNull() ?: 0
            it.province = province.text.toString().trim()
            it.country = country.text.toString().trim()
        }
        newAccount.contactNumber.let {
            it.areaCode = layoutContactEdit.telAreaCode.text.toString()
            it.contact = layoutContactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0
        }
        checkChanges(newAccount, update)
    }

    private fun checkChanges(newAccount: Account, update: (Map<String, Any?>) -> Unit) {
        val changes: HashMap<String, Any?> = HashMap()
        if (account.firstName != newAccount.firstName)
            changes[KEY_FIRST_NAME] = newAccount.firstName
        if (account.lastName != newAccount.lastName)
            changes[KEY_LAST_NAME] = newAccount.lastName
        if (account.address != newAccount.address)
            changes[KEY_ADDRESS] = newAccount.address
        if (account.contactNumber != newAccount.contactNumber)
            changes[KEY_CONTACT_NUMBER] = newAccount.contactNumber
        pickedImage?.let { changes[KEY_IMAGE] = account.image }

        if (changes.isNotEmpty()) update(changes)
    }

    private fun ActivityAccountInformationBinding.setupActionBar(){
        setSupportActionBar(tbTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tbTop.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun ActivityAccountInformationBinding.setFormData(){
        firstName.setText(account.firstName)
        lastName.setText(account.lastName)
        if (account.image.isNotEmpty()){
            profilePicture.loadProfile(account.image)
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

    private fun ActivityAccountInformationBinding.loadImage(){
        pickedImage?.let {
            profilePicture.loadProfile(it.toString())
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