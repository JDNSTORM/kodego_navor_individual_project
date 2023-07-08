package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career.CareerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile.SelectProfileDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.education.EducationFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile.ProfileFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.skills.SkillsFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ProfileAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ViewedProfileState
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.ViewPagerFragmentAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.ActivityProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.dialogs.ProgressDialog
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadProfile
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: ProfileViewModel by viewModels()
        binding.setupUI(
            viewModel.viewedProfileState,
            viewModel.accountProfiles,
            viewModel.action
        )
    }

    private fun ActivityProfileBinding.setupUI(
        state: StateFlow<ViewedProfileState>,
        accountProfiles: Flow<List<Profile>>?,
        action: (ProfileAction) -> StateFlow<RemoteState>?
    ){
        setupActionBar()
        loadProfile(state, accountProfiles, action)
    }

    private fun ActivityProfileBinding.loadProfile(
        state: StateFlow<ViewedProfileState>,
        accountProfiles: Flow<List<Profile>>?,
        action: (ProfileAction) -> StateFlow<RemoteState>?
    ) {
        val progressDialog = ProgressDialog(this@ProfileActivity, R.string.loading_data)
        lifecycleScope.launch {
            state.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{ profileState ->
                when(profileState){
                    is ViewedProfileState.Active -> {
                        setupViewPager()
                        monitorState(profileState.profile)
                        progressDialog.dismiss()
                    }
                    is ViewedProfileState.Inactive -> {
                        progressDialog.dismiss()
                        accountProfiles?.let {
                            selectProfile(it, action)
                        } ?: throwError()
                    }
                    is ViewedProfileState.Invalid -> {
                        progressDialog.dismiss()
                        throwError()
                    }
                    is ViewedProfileState.Loading -> progressDialog.show()
                }
            }
        }
    }

    private fun ActivityProfileBinding.monitorState(profile: Flow<Profile?>){
        lifecycleScope.launch {
            profile.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{bind(it)}
        }
    }

    private fun selectProfile(
        profiles: Flow<List<Profile>>,
        action: (ProfileAction) -> StateFlow<RemoteState>?
    ) {
        SelectProfileDialog(this, profiles, action).show()
    }

    private fun ActivityProfileBinding.setupActionBar(){
        setSupportActionBar(tbTop)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        tbTop.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun ActivityProfileBinding.bind(profile: Profile?){
        Log.d("Profile", profile.toString())
        profile?.let {
            layoutProfile.profilePicture.loadProfile(it.image)
            with(layoutProfile) {
                profileUserName.text = it.displayName()
                profession.text = it.profession
            }
        } ?: throwError()
    }

    private fun ActivityProfileBinding.setupViewPager() {
        val fragmentAdapter = ViewPagerFragmentAdapter(supportFragmentManager, lifecycle)
        fragmentAdapter.addFragment(ProfileFragment())
        fragmentAdapter.addFragment(CareerFragment())
        fragmentAdapter.addFragment(SkillsFragment())
        fragmentAdapter.addFragment(EducationFragment())

        with(viewpager2){
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = fragmentAdapter
        }
        TabLayoutMediator(tlNavBottom, viewpager2){ tab, position ->
            val (text, icon) = fragmentAdapter.fragments[position].tabInfo
            tab.text = text
            tab.setIcon(icon)
        }.attach()
    }

    private fun throwError(){
        Toast.makeText(this, "No Profile Selected!", Toast.LENGTH_SHORT).show()
        finish()
    }
}