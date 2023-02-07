package ph.kodego.navor_jamesdave.mydigitalprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.AccountFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.HomeFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.LoginFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ZoomOutPageTransformer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val loggedIn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)

        fragmentAdapter.addFragment(HomeFragment())

        if (loggedIn) {
            fragmentAdapter.addFragment(AccountFragment())
        }else{
            fragmentAdapter.addFragment(LoginFragment())
        }
//        val tabs: ArrayList<String> = arrayListOf("Home", "Private View", "Chats", "Account")
//        val tabs: ArrayList<String> = arrayListOf("Home", "Account")

        with(binding.viewPager2){
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = fragmentAdapter
        }

        TabLayoutMediator(binding.tlNavBottom, binding.viewPager2){
                tab, position ->
            var text: String = "Unknown"
            var icon: Int = R.drawable.ic_unknown_24
            fragmentAdapter.fragmentList[position].arguments?.let {
                text = it.getString("TabName").toString()
                icon = it.getInt("TabIcon")
            }
            tab.setText(text)
            tab.setIcon(icon)
        }.attach()
    }
}