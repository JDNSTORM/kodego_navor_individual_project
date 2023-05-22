package ph.kodego.navor_jamesdave.mydigitalprofile.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.AccountInformationActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.AccountSettingsActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentAccountBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseAccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: Dialog
    private lateinit var account: Account
    private lateinit var dao: FirebaseAccountDAOImpl

    init {
        if(this.arguments == null) {
            getTabInfo()
        }
    }
    private fun getTabInfo(){
        this.arguments = Bundle().apply {
            putString("TabName", "Account")
            putInt("TabIcon", R.drawable.ic_account_circle_24)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = FirebaseAccountDAOImpl(requireContext())
        progressDialog = ProgressDialog(requireContext(), R.string.loading_account)

        setAccount()

        binding.btnAccountInformation.setOnClickListener { goToAccountInformation() }
        binding.btnAccountSettings.setOnClickListener { goToAccountSettings() }
        binding.btnViewProfile.setOnClickListener { goToProfile() }
        binding.btnSignOut.setOnClickListener { signOut() }
    }

    private fun setAccount(){
        progressDialog.show()
        CoroutineScope(IO).launch {
            val firebaseAccount = async { dao.getAccount(dao.getCurrentUserID()) }.await()
            withContext(Main) {
                if (firebaseAccount != null) {
                    account = firebaseAccount
                    val fullName = account.fullName()
                    with(binding) {
                        profileUserName.text = fullName
                        email.text = account.contactInformation!!.emailAddress!!.email
                        GlideModule().loadProfilePhoto(binding.profilePicture, account.image)
                    }
                    progressDialog.dismiss()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(context, "Failed to get Account Data", Toast.LENGTH_LONG).show()
                    signOut()
                }
            }
        }
    }

    private fun goToAccountInformation(){
        val intent = Intent(context, AccountInformationActivity::class.java)
        Log.d("Account", account.toString())
        intent.putExtra(IntentBundles.Account, account)
        startActivity(intent)
        requireActivity().finish()
    }
    private fun goToAccountSettings(){
        val intent = Intent(context, AccountSettingsActivity::class.java)
        intent.putExtra(IntentBundles.Account, account)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun goToProfile(){
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra(IntentBundles.Account, account)
        startActivity(intent)
    }

    private fun signOut(){
        dao.signOutUser()
        val activity = requireActivity()
        val intent = activity.intent
        activity.finish()
        activity.startActivity(intent)
    }
}