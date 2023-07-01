package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.skills

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
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.SkillsMainAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentSkillsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.LayoutSkillEventsBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.expandFabs
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.minimizeFabs
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.SkillsEditingInterface
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class SkillsFragment(): ViewPagerFragment<FragmentSkillsBinding>() {
    private val viewModel: ProfileViewModel by viewModels()
    private val eventsBinding by lazy {
        LayoutSkillEventsBinding.inflate(layoutInflater, binding.root, true)
    }
    private val itemsAdapter = SkillsMainAdapter()
    private val setupMenu by lazy { setupMenu(requireActivity()) }
    private val touchHelper by lazy { itemsAdapter.activateTouchHelper() }
    private lateinit var profile: Profile
    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSkillsBinding {
        return FragmentSkillsBinding.inflate(inflater, container, false)
    }

    override fun getTabInformation(): TabInfo = TabInfo(
        "Skills",
        R.drawable.ic_skills_24
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(
            viewModel.viewedProfileState,
            viewModel.accountState,
            viewModel.action
        )
    }

    private fun setupUI(
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
                        itemsAdapter.setList(it.skills)
                    }else{
                        itemsAdapter.setList(emptyList())
                    }

                    if (uid == activeUID){
                        enableEditing{
                            val remoteState = action(ProfileAction.Update(it))!!
                            monitorState(remoteState)
                        }
                    }
                } ?: noActiveProfile()
            }
        }
    }

    private fun setupMenu(host: MenuHost){
        host.addMenuProvider(
            object: ListMenu(){
                override fun onOrganize(menuItem: MenuItem) {
                    when(itemsAdapter.toggleDrag()){
                        true -> {
                            touchHelper.attachToRecyclerView(binding.listSkills)
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

    private fun saveList() {
        val skills = itemsAdapter.skillsMainList()
        skills.lastIndex
        val changes: Map<String, Any?> = mapOf(Profile.KEY_SKILLS to skills)
        val remoteState =  viewModel.action(ProfileAction.Update(changes))!!
        monitorState(remoteState)
//        lifecycleScope.launch {
//            if(viewModel.updateProfile(profile, changes)){
//                Toast.makeText(requireContext(), "Skills Saved!", Toast.LENGTH_SHORT).show()
//            }else{
//                Toast.makeText(requireContext(), "Skills not saved", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun monitorState(state: StateFlow<RemoteState>){
        lifecycleScope.launch {
            state.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{
                when(it){
                    RemoteState.Success -> Toast.makeText(context, "Skills Saved!", Toast.LENGTH_SHORT).show()
                    RemoteState.Failed -> Toast.makeText(context, "Skills not saved", Toast.LENGTH_SHORT).show()
                    else -> {}
                }
            }
        }
    }

    private fun setupRecyclerView() {
        with(binding.listSkills){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemsAdapter
        }
        binding.showData()
    }

    private fun enableEditing(
        update: (Map<String, Any?>) -> Unit
    ) {
        setupMenu
        with(eventsBinding){
            minimizeFabs()
            efabSkillsOptions.setOnClickListener {
                if (efabSkillsOptions.isExtended){ minimizeFabs() }
                else{ expandFabs() }
            }
            layoutBackground.setOnClickListener { minimizeFabs() }
            btnAddMainCategory.setOnClickListener { SkillMainEditDialog(requireActivity(), profile, update).show() }
        }
        itemsAdapter.enableEditing(
            SkillsEditingInterface(
                eventsBinding,
                SkillMainEditDialog(requireActivity(), profile, update),
                SkillSubEditDialog(requireActivity(), profile, update)
            )
        )
    }

    private fun noActiveProfile() {
        Toast.makeText(requireContext(), "No Profile Detected!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
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