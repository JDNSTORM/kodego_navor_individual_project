package ph.kodego.navor_jamesdave.mydigitalprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.AboutAppDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseAccountDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.AccountFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.HomeFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.LoginFragment

@Deprecated("Replaced with MainActivity in activities")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fragmentAdapter:FragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbTop)

        fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)

        fragmentAdapter.addFragment(HomeFragment())
        val currentUserId = FirebaseAccountDAOImpl(this).getCurrentUserID()
        var accountFragment: AccountFragment? = null
        if (currentUserId.isNotEmpty()) {
            accountFragment = AccountFragment()
            fragmentAdapter.addFragment(accountFragment)
        }else{
            fragmentAdapter.addFragment(LoginFragment())
        }

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
            tab.text = text
            tab.setIcon(icon)
        }.attach()

        if(currentUserId.isNotEmpty()){
            binding.viewPager2.currentItem = fragmentAdapter.fragmentList.indexOf(accountFragment!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.btn_about -> {
                AboutAppDialog(this).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}