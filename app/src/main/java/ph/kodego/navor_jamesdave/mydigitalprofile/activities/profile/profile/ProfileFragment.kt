package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ProfileAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ViewedProfileState
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentProfileBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class ProfileFragment(): ViewPagerFragment<FragmentProfileBinding>(), FlowCollector<Profile?> {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var profile: Profile
    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(inflater, container, false)
    }

    override fun getTabInformation(): TabInfo = TabInfo(
        "Profile",
        R.drawable.ic_account_circle_24
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        binding.setupUI(
            viewModel.viewedProfileState,
            viewModel.accountState,
            viewModel.action
        )
    }

    private fun FragmentProfileBinding.setupUI(
        viewedProfileState: StateFlow<ViewedProfileState>,
        accountState: StateFlow<AccountState>,
        action: (ProfileAction) -> StateFlow<RemoteState>?
    ) {

    }

    private fun loadProfile() {
        lifecycleScope.launch {
            val refUID = viewModel.readActiveProfile()?.first()?.refUID
            val activeUID = Firebase.auth.currentUser?.uid
            Log.d("RefUID", refUID.toString())
            Log.d("ActiveUID", activeUID.toString())
            if(refUID == activeUID && activeUID != null){
                enableEditing()
            }
            viewModel.readActiveProfile()?.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)?.collect(this@ProfileFragment) ?: noActiveProfile()
        }
    }

    private fun enableEditing() {
        with(binding.btnEdit){
            isEnabled = true
            visibility = View.VISIBLE
            setOnClickListener { ProfileEditDialog(requireActivity(), profile).show() }
        }
    }

    private fun noActiveProfile() {
        Toast.makeText(requireContext(), "No Profile Detected!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    override suspend fun emit(value: Profile?) {
        value?.let {
            profile = it
            setProfileDetails()
        } ?: noActiveProfile()
    }

    private fun setProfileDetails() {
        with(binding){
            address.text = profile.address.localAddress()
            email.text = profile.emailAddress
            professionalSummary.text = profile.profileSummary
            binding.professionalSummary.visibility = View.VISIBLE
            binding.loadingData.visibility = View.GONE
        }
    }
}