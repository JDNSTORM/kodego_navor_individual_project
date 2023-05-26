package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.Manifest.permission.READ_MEDIA_IMAGES
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.MainActivity
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
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog

class AccountInformationActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAccountInformationBinding.inflate(layoutInflater) }
    private lateinit var account: Account
    private val progressDialog: Dialog by lazy { ProgressDialog(binding.root.context, R.string.updating_account) }
    private lateinit var accountDAO: FirebaseAccountDAOImpl
    private lateinit var contactInformationDAO: FirebaseContactInformationDAOImpl

    private val openDocumentLauncher = registerForActivityResult(OpenDocumentContract()) { uri ->
        uri?.let { onImageSelected(it) } ?: Log.e("Document", "Image Pick Cancelled")
    }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        binding.profilePicture.setOnClickListener { chooseProfilePicture() }
        binding.btnSave.setOnClickListener { checkFormData() }
        onBackPressedDispatcher.addCallback { goToMain() }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.tbTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbTop.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setFormData(){
        val address = account.contactInformation!!.address
        val contactNumber = account.contactInformation!!.contactNumber
        binding.firstName.setText(account.firstName)
        binding.lastName.setText(account.lastName)
        Log.d("Address", address.toString())
        Log.d("Email", account.contactInformation!!.emailAddress.toString())
        if (account.image.isNotEmpty() && account.image != "null"){
            loadProfilePhoto()
        }
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

    private fun chooseProfilePicture(){
        val choosePictureIntent = Intent(
            ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        pictureChosen.launch(choosePictureIntent)

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

    private fun requestMediaImagesPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, READ_MEDIA_IMAGES)){
            AlertDialog.Builder(this)
                .setTitle(R.string.permission_required)
                .setMessage("This Permission is required to Read Media Storage. Grant Permission?")
                .setPositiveButton(R.string.dialog_yes){ dialog, _ ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(this, arrayOf(READ_MEDIA_IMAGES), MEDIA_IMAGES_REQUEST_CODE)
                    }
                    dialog.dismiss()
                }.setNegativeButton(R.string.dialog_no){ dialog, _ ->
                    dialog.dismiss()
                }.show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(READ_MEDIA_IMAGES), MEDIA_IMAGES_REQUEST_CODE)
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
            contact = binding.layoutContactEdit.telContactNumber.text.toString().toLongOrNull() ?: 0
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
        if (updatedAccountData.isNotEmpty() || updatedAddressData.isNotEmpty() || updatedContactNumberData.isNotEmpty()){
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
                    goToMain()
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
        progressDialog.show()
        lifecycleScope.launch {
            if (FirebaseStorageDAOImpl(this@AccountInformationActivity).updateAccountPhoto(uri)){
                loadProfilePhoto()
            }
            progressDialog.dismiss()
        }
    }

    private fun loadProfilePhoto(){
        Glide
            .with(binding.root.context)
            .load(Firebase.auth.currentUser!!.photoUrl)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(binding.profilePicture)
    }

    private fun goToMain(){ //TODO: Use ActivityForResult
        val intent = Intent(baseContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (
            requestCode == MEDIA_IMAGES_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PERMISSION_GRANTED
        ){
            chooseProfilePicture()
        }else{
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val MEDIA_IMAGES_REQUEST_CODE = 1
    }
}