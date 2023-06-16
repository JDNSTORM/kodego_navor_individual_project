package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.account.AccountFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.home.HomeFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.sign_in.SignInFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewPagerFragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class MainActivity(): AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val fragmentAdapter by lazy { ViewPagerFragmentAdapter(supportFragmentManager, lifecycle) }
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.tbTop)
        setupMenu()
        setupViewPager()
    }

    private fun setupMenu(){
        addMenuProvider(MainMenu(this), this, Lifecycle.State.RESUMED)
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

    override fun onResume() {
        viewModel.clearActiveProfile()
        super.onResume()
    }
}