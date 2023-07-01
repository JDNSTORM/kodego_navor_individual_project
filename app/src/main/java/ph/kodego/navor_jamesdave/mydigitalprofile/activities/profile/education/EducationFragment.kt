package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.education

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ListMenu
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ProfileAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ViewedProfileState
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.EducationsAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class EducationFragment(): ViewPagerFragment<FragmentEducationBinding>() {
    private val viewModel: ProfileViewModel by viewModels()
    private val itemsAdapter = EducationsAdapter()
    private val setupMenu by lazy { setupMenu(requireActivity()) }
    private val touchHelper by lazy { itemsAdapter.activateTouchHelper() }
    private lateinit var profile: Profile
    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentEducationBinding {
        return FragmentEducationBinding.inflate(inflater, container, false)
    }

    override fun getTabInformation(): TabInfo = TabInfo(
        "Education",
        R.drawable.ic_education_24
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setupUI(
            viewModel.viewedProfileState,
            viewModel.accountState,
            viewModel.action
        )
    }

    private fun FragmentEducationBinding.setupUI(
        state: StateFlow<ViewedProfileState>,
        accountState: StateFlow<AccountState>,
        action: (ProfileAction) -> StateFlow<RemoteState>?
    ) {
        setupRecyclerView()
        val (flow, uid) = (state.value as ViewedProfileState.Active)
        val activeUID = (accountState.value as? AccountState.Active)?.uid

        lifecycleScope.launch {
            flow.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{
                it?.let {
                    profile = it
                    if (it.careers.isNotEmpty()) {
                        itemsAdapter.setList(it.educations)
                    }else{
                        itemsAdapter.setList(emptyList())
                    }
                } ?: noActiveProfile()
            }
        }

        if (uid == activeUID){
            enableEditing{
                val remoteState = action(ProfileAction.Update(it))!!
                monitorState(remoteState)
            }
        }
    }

    private fun monitorState(state: StateFlow<RemoteState>){
        lifecycleScope.launch {
            state.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{
                when(it){
                    RemoteState.Success -> Toast.makeText(context, "Educations Saved!", Toast.LENGTH_SHORT).show()
                    RemoteState.Failed -> Toast.makeText(context, "Educations not saved", Toast.LENGTH_SHORT).show()
                    else -> {}
                }
            }
        }
    }

    private fun setupMenu(host: MenuHost){
        host.addMenuProvider(
            object: ListMenu(){
                override fun onOrganize(menuItem: MenuItem) {
                    when(itemsAdapter.toggleDrag()){
                        true -> {
                            touchHelper.attachToRecyclerView(binding.listEducation)
                            menuItem.setIcon(R.drawable.ic_save_24)
                        }
                        false -> {
                            saveList()
                            touchHelper.attachToRecyclerView(null)
                            menuItem.setIcon(R.drawable.ic_format_list_24)
                        }
                    }
                }
            },
            viewLifecycleOwner, Lifecycle.State.RESUMED
        )
    }

    private fun saveList(){
        val educations = itemsAdapter.educations()
        educations.lastIndex
        val changes: Map<String, Any?> = mapOf(Profile.KEY_EDUCATIONS to educations)
        val remoteState = viewModel.action(ProfileAction.Update(changes))!!
        monitorState(remoteState)
//        lifecycleScope.launch {
//            if(viewModel.updateProfile(profile, changes)){
//                Toast.makeText(requireContext(), "Educations Saved!", Toast.LENGTH_SHORT).show()
//            }else{
//                Toast.makeText(requireContext(), "Educations not saved", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun noActiveProfile() {
        Toast.makeText(requireContext(), "No Profile Detected!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    private fun setupRecyclerView() {
        with(binding.listEducation){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemsAdapter
        }
        binding.showData()
    }

    private fun FragmentEducationBinding.enableEditing(update: (Map<String, Any?>) -> Unit) {
        setupMenu
        with(btnAdd){
            isEnabled = true
            visibility = View.VISIBLE
            setOnClickListener {
                EducationEditDialog(requireActivity(), profile, update).show()
            }
        }
        itemsAdapter.enableEditing(EducationEditDialog(requireActivity(), profile, update))
    }

    private fun resetRecyclerViewState(){
        itemsAdapter.clearToggle()
        touchHelper.attachToRecyclerView(null)
    }

    override fun onPause() {
        resetRecyclerViewState()
        super.onPause()
    }
}