package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.*
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ZoomOutPageTransformer

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)
        val tabs: ArrayList<String> = ArrayList()

        fragmentAdapter.addFragment(ProfileFragment())
        tabs.add(ProfileFragment.fragmentName)
        fragmentAdapter.addFragment(CareerFragment())
        tabs.add(CareerFragment.fragmentName)
        fragmentAdapter.addFragment(SkillsFragment())
        tabs.add(SkillsFragment.fragmentName)
        fragmentAdapter.addFragment(EducationFragment())
        tabs.add(EducationFragment.fragmentName)

//        val tabs: ArrayList<String> = arrayListOf("Profile", "Career", "Skills", "Education", "More")
//        val tabs: ArrayList<String> = arrayListOf("Profile", "Career", "Skills", "Education")

        with(binding.viewPager2){
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = fragmentAdapter
        }

        TabLayoutMediator(binding.tlNavBottom, binding.viewPager2){
                tab, position ->
            tab.text = tabs[position]
        }.attach()
    }
}