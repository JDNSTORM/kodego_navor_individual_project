package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.account.AccountFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.home.HomeFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.sign_in.SignInFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.HomeAction
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewPagerFragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityMainBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles.EXTRA_SIGNED_IN
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.AccountViewModel
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.HomeViewModel

@AndroidEntryPoint
class MainActivity(): AppCompatActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbTop)
        setupMenu{
            val accountID = getString(R.string.account_id)
            val profileID = getString(R.string.profile_id)
            val profile = Profile(profileID = profileID, refUID = accountID)
            val intent = Intent(this, ProfileActivity::class.java)
            viewModel.action(HomeAction.View(profile))
            startActivity(intent)
        }
        binding.setupViewPager()
    }

    private fun setupMenu(viewProfile: () -> Unit){
        addMenuProvider(MainMenu(this, viewProfile), this, Lifecycle.State.RESUMED)
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
        val accountViewModel: AccountViewModel by viewModels()
        accountViewModel //Initialize
        viewModel.action(HomeAction.View())
        super.onResume()
    }
}