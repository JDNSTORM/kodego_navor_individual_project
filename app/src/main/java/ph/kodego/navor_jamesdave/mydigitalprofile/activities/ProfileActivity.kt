package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
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
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.IntentBundles

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profile: Profile
    private lateinit var dao: FirebaseProfileDAOImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        dao = FirebaseProfileDAOImpl(binding.root.context)

        val profileBundle = Bundle()
        lifecycleScope.launch {
            if (intent.hasExtra(IntentBundles.Profile)) {
                profile = intent.getParcelableExtra(IntentBundles.Profile)!!
//            profile = intent.getParcelableExtra(Constants.BundleProfile, Profile::class.java)!! //TODO: For API 33
            } else if (intent.hasExtra(IntentBundles.Account)) {
                val account: Account = intent.getParcelableExtra(IntentBundles.Account)!!
                profile = dao.getProfile(account.uID)
                profile.setAccount(account)
            } else {
                Toast.makeText(applicationContext, "No Profile Selected!", Toast.LENGTH_SHORT).show()
                finish()
            }
            profileBundle.putParcelable(IntentBundles.Profile, profile)
            setProfileDetails()

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
    private fun setupActionBar(){
        setSupportActionBar(binding.tbTop)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbTop.setNavigationOnClickListener {
//            onBackPressedDispatcher //TODO: Implement
            onBackPressed()
        }
    }

    private fun setProfileDetails(){
        GlideModule().loadProfilePhoto(binding.viewholderProfile.profilePicture, profile.image)
        with(binding.viewholderProfile) {
            profileUserName.text = "${profile.firstName} ${profile.lastName}"
            profession.text = profile.profession
        }
    }
    fun updateProfile(){
        lifecycleScope.launch {
            val updatedProfile = dao.getProfile(profile.uID)
            updatedProfile.setAccount(profile)
            profile = updatedProfile
            setProfileDetails()
        }
    }
}