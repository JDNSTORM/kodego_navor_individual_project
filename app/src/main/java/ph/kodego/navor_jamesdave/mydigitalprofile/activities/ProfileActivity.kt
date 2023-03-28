package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfileDAO
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.FirebaseProfileDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile.CareerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile.EducationFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile.ProfileFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile.SkillsFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Account
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profile: Profile
    private lateinit var dao: FirebaseProfileDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.tbTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbTop.setNavigationOnClickListener {
//            onBackPressedDispatcher
            onBackPressed()
        }
        dao = FirebaseProfileDAOImpl(binding.root.context)

        val profileBundle = Bundle()
        lifecycleScope.launch {
            if (intent.hasExtra(Constants.BundleProfile)) {//TODO: Proper Data Handling
                profile = intent.getParcelableExtra(Constants.BundleProfile)!!
//            profile = intent.getParcelableExtra(Constants.BundleProfile, Profile::class.java)!! //TODO: For API 33
            } else if (intent.hasExtra(Constants.BundleAccount)) { //TODO: Retrieve Profile
                val account: Account = intent.getParcelableExtra(Constants.BundleAccount)!!
                profile = dao.getProfile(account.uID)
                profile.setAccount(account)
            } else { //TODO: Throw Error and Finish Activity
                profile = Profile()
            }
            profileBundle.putParcelable(Constants.BundleProfile, profile)

            with(binding.viewholderProfile) {
                profilePicture.setImageResource(profile.profilePicture)
                profileUserName.text = "${profile.firstName} ${profile.lastName}"
                profession.text = profile.profession
            }

            val fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)

            fragmentAdapter.addFragment(ProfileFragment())
            fragmentAdapter.addFragment(CareerFragment())
            fragmentAdapter.addFragment(SkillsFragment())
            fragmentAdapter.addFragment(EducationFragment())

            with(binding.viewpager2) {
                orientation = ViewPager2.ORIENTATION_HORIZONTAL
                adapter = fragmentAdapter
            }

            TabLayoutMediator(binding.tlNavBottom, binding.viewpager2) { tab, position ->
                var text: String = "Unknown"
                var icon: Int = R.drawable.ic_unknown_24
                with(fragmentAdapter.fragmentList[position]) {
                    arguments?.let {
                        text = it.getString("TabName") ?: "Unknown"
                        icon =
                            if (it.getInt("TabIcon") != 0) it.getInt("TabIcon") else R.drawable.ic_unknown_24
                    }
                    arguments = profileBundle
                }
                tab.text = text
                tab.setIcon(icon)
            }.attach()
        }
    }
}