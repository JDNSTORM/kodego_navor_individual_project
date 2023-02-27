package ph.kodego.navor_jamesdave.mydigitalprofile.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ph.kodego.navor_jamesdave.mydigitalprofile.MainActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.AccountInformationActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.AccountSettingsActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProgressBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentAccountBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.Firebase
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FirebaseAccountInterface

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: Dialog

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

        Firebase(firebaseInterface).getAccount()
        binding.btnAccountInformation.setOnClickListener {
            goToAccountInformation()
        }
        binding.btnAccountSettings.setOnClickListener {
            goToAccountSettings()
        }
        binding.btnSignOut.setOnClickListener {
//            val fragmentAdapter = (requireActivity() as MainActivity).getFragmentAdapter()
//            val viewPager = (requireActivity() as MainActivity).getViewPager()
//            fragmentAdapter.fragmentList.remove(this)
////            fragmentAdapter.notifyItemRemoved(fragmentAdapter.fragmentList.indexOf(this))
//            fragmentAdapter.addFragment(LoginFragment())
//            fragmentAdapter.notifyItemInserted(fragmentAdapter.fragmentList.indexOf(LoginFragment()))
//            viewPager.adapter = fragmentAdapter
            signOut()
        }
    }

    private fun goToAccountInformation(){
        val intent = Intent(context, AccountInformationActivity::class.java)
        startActivity(intent)
    }
    private fun goToAccountSettings(){
        val intent = Intent(context, AccountSettingsActivity::class.java)
        startActivity(intent)
    }

    private fun signOut(){
        Firebase().signOutUser()
        val activity = requireActivity()
        val intent = activity.intent
        activity.finish()
        activity.startActivity(intent)
    }

    private val firebaseInterface = object: FirebaseAccountInterface{
        override fun getAccountSuccess(account: Account) {
            val fullName = "${account.firstName} ${account.lastName}"
            with(binding){
                profileUserName.text = fullName
                email.text = account.email
            }
        }

        override fun getAccountFailed() {
            Toast.makeText(context, "Failed to get Account Data", Toast.LENGTH_LONG).show()
            signOut()
        }

        override fun showProgressDialog() {
            progressDialog = Dialog(requireContext())
            val progressBinding = DialogueProgressBinding.inflate(layoutInflater)
            progressBinding.progressText.setText(R.string.loading_account)
            progressDialog.setContentView(progressBinding.root)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun hideProgressDialog() {
            progressDialog.dismiss()
        }

    }
}