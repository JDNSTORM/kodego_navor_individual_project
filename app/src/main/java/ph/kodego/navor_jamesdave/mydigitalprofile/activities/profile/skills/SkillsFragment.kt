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
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class SkillsFragment(): ViewPagerFragment<FragmentSkillsBinding>(), FlowCollector<Profile?> {
    private val viewModel: ProfileViewModel by viewModels()
    private val eventsBinding by lazy {
        LayoutSkillEventsBinding.inflate(layoutInflater, binding.root, true)
    }
    private val itemsAdapter by lazy { SkillsMainAdapter() }
    private val activeUID = Firebase.auth.currentUser?.uid
    private val setupMenu by lazy { setupMenu(requireActivity()) }

    override fun getTabInformation(): TabInfo = TabInfo(
        "Skills",
        R.drawable.ic_skills_24
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSkillsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
    }

    private fun setupMenu(host: MenuHost){
        host.addMenuProvider(
            object: ListMenu(){
                override fun onOrganize(menuItem: MenuItem) {
                    when(itemsAdapter.toggleDrag()){
                        true -> menuItem.setIcon(R.drawable.ic_save_24)
                        false -> menuItem.setIcon(R.drawable.ic_format_list_24)
                    }
                }
            },
            viewLifecycleOwner, Lifecycle.State.RESUMED
        )
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

    private fun enableEditing(profile: Profile) {
        with(eventsBinding){
            minimizeFabs()
            efabSkillsOptions.setOnClickListener {
                if (efabSkillsOptions.isExtended){ minimizeFabs() }
                else{ expandFabs() }
            }
            root.setOnClickListener { minimizeFabs() }
            btnAddMainCategory.setOnClickListener { DialogSkillMainEdit(requireActivity(), profile).show() }
        }
    }

    private fun noActiveProfile() {
        Toast.makeText(requireContext(), "No Profile Detected!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    override suspend fun emit(value: Profile?) {
        value?.let {
            itemsAdapter.setList(it.skills)
            if (it.refUID == activeUID){
                enableEditing(it)
                setupMenu
            }
        } ?: noActiveProfile()
    }

    override fun onPause() {
        itemsAdapter.clearToggle()
        super.onPause()
    }
}