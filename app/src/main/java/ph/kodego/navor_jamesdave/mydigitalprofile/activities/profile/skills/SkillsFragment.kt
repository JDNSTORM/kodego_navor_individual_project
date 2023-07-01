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
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ListMenu
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
class SkillsFragment(): ViewPagerFragment<FragmentSkillsBinding>(), FlowCollector<Profile?> {
    private val viewModel: ProfileViewModel by viewModels()
    private val eventsBinding by lazy {
        LayoutSkillEventsBinding.inflate(layoutInflater, binding.root, true)
    }
    private val itemsAdapter = SkillsMainAdapter()
    private val activeUID = Firebase.auth.currentUser?.uid
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
        loadProfile()
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
//        val skills = itemsAdapter.skillsMainList()
//        skills.lastIndex
//        val changes: Map<String, Any?> = mapOf(Profile.KEY_SKILLS to skills)
//        lifecycleScope.launch {
//            if(viewModel.updateProfile(profile, changes)){
//                Toast.makeText(requireContext(), "Skills Saved!", Toast.LENGTH_SHORT).show()
//            }else{
//                Toast.makeText(requireContext(), "Skills not saved", Toast.LENGTH_SHORT).show()
//            }
//        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            viewModel.readActiveProfile()?.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)?.let {
                setupRecyclerView()
                it.collect(this@SkillsFragment)
            } ?: noActiveProfile()
        }
    }

    private fun setupRecyclerView() {
        with(binding.listSkills){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemsAdapter
        }
        binding.showData()
    }

    private fun enableEditing() {
        with(eventsBinding){
            minimizeFabs()
            efabSkillsOptions.setOnClickListener {
                if (efabSkillsOptions.isExtended){ minimizeFabs() }
                else{ expandFabs() }
            }
            layoutBackground.setOnClickListener { minimizeFabs() }
            btnAddMainCategory.setOnClickListener { SkillMainEditDialog(requireActivity(), profile).show() }
        }
        itemsAdapter.enableEditing(
            SkillsEditingInterface(
                eventsBinding,
                SkillMainEditDialog(requireActivity(), profile),
                SkillSubEditDialog(requireActivity(), profile)
            )
        )
    }

    private fun noActiveProfile() {
//        Toast.makeText(requireContext(), "No Profile Detected!", Toast.LENGTH_SHORT).show()
//        requireActivity().finish()
    }

    override suspend fun emit(value: Profile?) {
        value?.let {
            profile = it
            itemsAdapter.setList(it.skills)
            if (it.refUID == activeUID){
                enableEditing()
                setupMenu
            }
        } ?: noActiveProfile()
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