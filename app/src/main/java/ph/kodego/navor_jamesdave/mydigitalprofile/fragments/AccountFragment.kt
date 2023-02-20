package ph.kodego.navor_jamesdave.mydigitalprofile.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.kodego.navor_jamesdave.mydigitalprofile.MainActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.AccountInformationActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.AccountSettingsActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

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
        binding.btnAccountInformation.setOnClickListener {
            goToAccountInformation()
        }
        binding.btnAccountSettings.setOnClickListener {
            goToAccountSettings()
        }
        binding.btnLogout.setOnClickListener {
            val fragmentAdapter = (requireActivity() as MainActivity).getFragmentAdapter()
            val viewPager = (requireActivity() as MainActivity).getViewPager()
            fragmentAdapter.fragmentList.remove(this)
//            fragmentAdapter.notifyItemRemoved(fragmentAdapter.fragmentList.indexOf(this))
            fragmentAdapter.addFragment(LoginFragment())
            fragmentAdapter.notifyItemInserted(fragmentAdapter.fragmentList.indexOf(LoginFragment()))
            viewPager.adapter = fragmentAdapter
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
}