package ph.kodego.navor_jamesdave.mydigitalprofile.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.HomeFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.fragments.ProfileFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.ZoomOutPageTransformer

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle)
        fragmentAdapter.addFragment(ProfileFragment())

        val tabs: ArrayList<String> = arrayListOf("Profile", "Career", "Skills", "Education", "More")

        with(binding.viewPager2){
            this.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            setPageTransformer(ZoomOutPageTransformer())
            adapter = fragmentAdapter
        }

        TabLayoutMediator(binding.tlNavBottom, binding.viewPager2){
                tab, position ->
            tab.text = tabs[position]
        }.attach()
    }
}