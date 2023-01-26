package ph.kodego.navor_jamesdave.mydigitalprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.HomeFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.LoginFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ZoomOutPageTransformer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)
        val tabs: ArrayList<String> = ArrayList()

        fragmentAdapter.addFragment(HomeFragment())
        tabs.add(HomeFragment.fragmentName)
        fragmentAdapter.addFragment(LoginFragment())
        tabs.add(LoginFragment.fragmentName)

//        val tabs: ArrayList<String> = arrayListOf("Home", "Private View", "Chats", "Account")
//        val tabs: ArrayList<String> = arrayListOf("Home", "Account")

        with(binding.viewPager2){
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = fragmentAdapter
        }

        TabLayoutMediator(binding.tlNavBottom, binding.viewPager2){
                tab, position ->
            tab.text = tabs[position]
        }.attach()
    }
}