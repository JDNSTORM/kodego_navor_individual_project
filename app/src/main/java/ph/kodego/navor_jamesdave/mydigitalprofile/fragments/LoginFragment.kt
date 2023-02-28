package ph.kodego.navor_jamesdave.mydigitalprofile.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.CreateAccountActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.DialogueProgressBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentLoginBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.Firebase
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseLoginInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.FormControls

class LoginFragment() : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: Dialog
    private lateinit var formControls: FormControls

    init {
        if(this.arguments == null) {
            getTabInfo()
        }
    }
    private fun getTabInfo(){
        this.arguments = Bundle().apply {
            putString("TabName", "Login")
            putInt("TabIcon", R.drawable.ic_switch_account_24)
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formControls = FormControls() //TODO: Validate Fields upon losing focus
        binding.btnSignIn.setOnClickListener {
//            val fragmentAdapter = (requireActivity() as MainActivity).getFragmentAdapter()
//            val viewPager = (requireActivity() as MainActivity).getViewPager()
//            fragmentAdapter.fragmentList.remove(this)
////            fragmentAdapter.notifyItemRemoved(fragmentAdapter.fragmentList.indexOf(this))
//            fragmentAdapter.addFragment(AccountFragment())
//            fragmentAdapter.notifyItemInserted(fragmentAdapter.fragmentList.indexOf(AccountFragment()))
//            viewPager.adapter = fragmentAdapter

            validateForm()
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(context, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateForm(){
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString()
        with(formControls) {
            when (false) {
                validateEmail(email) -> binding.email.requestFocus()
                validateText(password) -> binding.password.requestFocus()
                else -> Firebase(firebaseInterface).signInUser(email, password)
            }
        }
    }

    private val firebaseInterface = object: FirebaseLoginInterface {
        override fun signInSuccessful() {
            Toast.makeText(context, "Sign In Successful", Toast.LENGTH_SHORT).show()
            val activity = requireActivity()
            val intent = activity.intent
            progressDialog.cancel()
            activity.finish()
            activity.startActivity(intent)
        }

        override fun signInFailed(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        override fun showProgressDialog() {
            progressDialog = Dialog(requireContext())
            val progressBinding = DialogueProgressBinding.inflate(layoutInflater)
            progressBinding.progressText.setText(R.string.signing_in)
            progressDialog.setContentView(progressBinding.root)
            progressDialog.setCancelable(false)
            progressDialog.show()
        }
        override fun hideProgressDialog() {
            progressDialog.dismiss()
        }
    }
}