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
import ph.kodego.navor_jamesdave.mydigitalprofile.models.User

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var profile: ProfileData? = null

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

        val userData = Bundle()
        if(intent.hasExtra("Profile")){//TODO: Proper Data Handling
            profile = intent.getSerializableExtra("Profile") as ProfileData
            userData.putSerializable("Profile", profile)
        }
        if(profile != null){
            with(binding.viewholderProfile) {
                profilePicture.setImageResource(profile!!.user.profilePicture)
                profileUserName.text = "${profile!!.user.firstName} ${profile!!.user.lastName}"
                profession.text = profile!!.profession
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
            fragmentAdapter.fragmentList[position].arguments = userData
            tab.setText(text)
            tab.setIcon(icon)
        }.attach()
    }
}