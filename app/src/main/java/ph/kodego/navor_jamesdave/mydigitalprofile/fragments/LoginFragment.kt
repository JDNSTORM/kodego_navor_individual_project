package ph.kodego.navor_jamesdave.mydigitalprofile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentLoginBinding

class LoginFragment(private val fragmentAdapter: FragmentAdapter, private val viewPager: ViewPager2) : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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
        binding.btnLogin.setOnClickListener {
            fragmentAdapter.fragmentList.remove(this)
            fragmentAdapter.notifyItemRemoved(fragmentAdapter.itemCount)
            fragmentAdapter.addFragment(AccountFragment())
            fragmentAdapter.notifyItemInserted(fragmentAdapter.itemCount)
            viewPager.adapter = fragmentAdapter
        }
    }
}