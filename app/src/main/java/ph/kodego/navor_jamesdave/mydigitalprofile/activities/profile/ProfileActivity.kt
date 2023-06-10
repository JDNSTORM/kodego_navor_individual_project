package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.FragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewPagerFragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.utils.GlideModule
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity(), FlowCollector<Profile?> {
    private val binding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private val viewModel: ProfileViewModel by viewModels()
    private val progressDialog by lazy { ProgressDialog(this, R.string.loading_data) }
    private val selectProfileDialog by lazy {
        val dialog = SelectProfileDialog(this)
        dialog.setOnDismissListener {
            viewModel.readActiveProfile()?.let {
                loadProfile()
            } ?: throwError()
        }
        dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupActionBar()
        loadProfile()
    }

    private fun loadProfile() {
        progressDialog.show()
        lifecycleScope.launch {
            viewModel.readActiveProfile()?.let {
                setupViewPager()
                it.collect(this@ProfileActivity)
            } ?: run {
                selectProfile()
            }
        }
    }

    private fun setupViewPager() {
        val fragmentAdapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        fragmentAdapter.addFragment(ProfileFragment())
        fragmentAdapter.addFragment(CareerFragment())
        fragmentAdapter.addFragment(SkillsFragment())
        fragmentAdapter.addFragment(EducationFragment())

        with(binding.viewpager2){
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = fragmentAdapter
        }
        TabLayoutMediator(binding.tlNavBottom, binding.viewpager2){ tab, position ->
            val (text, icon) = fragmentAdapter.fragments[position].tabInfo
            tab.text = text
            tab.setIcon(icon)
        }.attach()
    }

    private fun selectProfile() {
        progressDialog.dismiss()
        selectProfileDialog.show()
    }

    private fun setupActionBar(){
        setSupportActionBar(binding.tbTop)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.tbTop.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override suspend fun emit(value: Profile?) {
        value?.let {
            setProfileDetails(it)
        } ?: throwError()
    }

    private fun setProfileDetails(profile: Profile){
        GlideModule().loadProfilePhoto(binding.viewholderProfile.profilePicture, profile.image)
        with(binding.viewholderProfile) {
            profileUserName.text = profile.displayName()
            profession.text = profile.profession
        }
        progressDialog.dismiss()
    }

    private fun throwError(){
        Toast.makeText(this, "Profile Inaccessible!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onDestroy() {
        viewModel.clearActiveProfile()
        super.onDestroy()
    }
}