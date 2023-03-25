package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile.CareerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile.EducationFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile.ProfileFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments_profile.SkillsFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfileData
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.Constants

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profile: Profile

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

        val profile = Bundle()
        if(intent.hasExtra(Constants.BundleProfile)){//TODO: Proper Data Handling
            this.profile = intent.getParcelableExtra<Profile>(Constants.BundleProfile)!!
            profile.putParcelable(Constants.BundleProfile, this.profile) //TODO: Change Serializable
        }else{
            this.profile = Profile()
        }
        with(binding.viewholderProfile) {
            profilePicture.setImageResource(this@ProfileActivity.profile.profilePicture)
            profileUserName.text = "${this@ProfileActivity.profile.firstName} ${this@ProfileActivity.profile.lastName}"
            profession.text = this@ProfileActivity.profile.profession
        }

        val fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)

        fragmentAdapter.addFragment(ProfileFragment())
        fragmentAdapter.addFragment(CareerFragment())
        fragmentAdapter.addFragment(SkillsFragment())
        fragmentAdapter.addFragment(EducationFragment())

        with(binding.viewpager2){
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = fragmentAdapter
        }

        TabLayoutMediator(binding.tlNavBottom, binding.viewpager2){
                tab, position ->
            var text: String = "Unknown"
            var icon: Int = R.drawable.ic_unknown_24
            with(fragmentAdapter.fragmentList[position]) {
                arguments?.let {
                    text = it.getString("TabName") ?: "Unknown"
                    icon = if (it.getInt("TabIcon") != 0) it.getInt("TabIcon") else R.drawable.ic_unknown_24
                }
                arguments = profile
            }
            tab.text = text
            tab.setIcon(icon)
        }.attach()
    }
}