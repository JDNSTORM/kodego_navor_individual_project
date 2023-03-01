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
import ph.kodego.navor_jamesdave.mydigitalprofile.models.ProfileData

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var profileData: ProfileData? = null

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
        if(intent.hasExtra("ProfileData")){//TODO: Proper Data Handling
            profileData = intent.getSerializableExtra("ProfileData") as ProfileData
            profile.putSerializable("Profile", profileData!!.profile)
        }
        if(profileData != null){
            with(binding.viewholderProfile) {
                profilePicture.setImageResource(profileData!!.user.profilePicture)
                profileUserName.text = "${profileData!!.user.firstName} ${profileData!!.user.lastName}"
                profession.text = profileData!!.profile.profession
            }
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
            fragmentAdapter.fragmentList[position].arguments?.let {
                text = it.getString("TabName").toString()
                icon = it.getInt("TabIcon")
            }
            fragmentAdapter.fragmentList[position].arguments = profile
            tab.setText(text)
            tab.setIcon(icon)
        }.attach()
    }
}