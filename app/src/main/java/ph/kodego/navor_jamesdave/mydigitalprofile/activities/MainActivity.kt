package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.account.AccountFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.home.HomeFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.sign_in.SignInFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewPagerFragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.AboutAppDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel

@AndroidEntryPoint
class MainActivity(): AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val fragmentAdapter by lazy { ViewPagerFragmentAdapter(supportFragmentManager, lifecycle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.tbTop)

        setupViewPager()
    }

    private fun setupViewPager() {
        fragmentAdapter.addFragment(HomeFragment())
        FirebaseAuth.getInstance().currentUser?.let {
            fragmentAdapter.addFragment(AccountFragment())
        } ?: fragmentAdapter.addFragment(SignInFragment())

        with(binding.viewPager2){
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = fragmentAdapter
        }
        TabLayoutMediator(binding.tlNavBottom, binding.viewPager2){ tab, position ->
            val (text, icon) = fragmentAdapter.fragments[position].tabInfo
            tab.text = text
            tab.setIcon(icon)
        }.attach()
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