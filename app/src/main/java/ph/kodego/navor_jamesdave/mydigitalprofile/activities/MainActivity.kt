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
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.HomeAction
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewPagerFragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles.EXTRA_SIGNED_IN
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.HomeViewModel
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class MainActivity(): AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbTop)
        setupMenu()
        binding.setupViewPager()
    }

    private fun setupMenu(){
        addMenuProvider(MainMenu(this), this, Lifecycle.State.RESUMED)
    }

    private fun ActivityMainBinding.setupViewPager() {
        val fragmentAdapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        fragmentAdapter.addFragment(HomeFragment())
        FirebaseAuth.getInstance().currentUser?.let {
            fragmentAdapter.addFragment(AccountFragment())
        } ?: fragmentAdapter.addFragment(SignInFragment())

        with(viewPager2){
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = fragmentAdapter
        }
        TabLayoutMediator(tlNavBottom, viewPager2){ tab, position ->
            val (text, icon) = fragmentAdapter.fragments[position].tabInfo
            tab.text = text
            tab.setIcon(icon)
        }.attach()
        val signedIn = intent.getBooleanExtra(EXTRA_SIGNED_IN, false)
        if (signedIn){
            viewPager2.currentItem = fragmentAdapter.fragments.lastIndex
        }
    }

    override fun onResume() {
        val viewModel: ProfileViewModel by viewModels()
        viewModel.clearActiveProfile()
        val homeViewModel: HomeViewModel by viewModels()
        homeViewModel.action(HomeAction.View())
        super.onResume()
    }
}