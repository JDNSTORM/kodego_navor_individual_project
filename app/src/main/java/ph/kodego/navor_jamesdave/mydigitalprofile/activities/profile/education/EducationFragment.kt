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
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ListMenu
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.EducationsAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentEducationBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class EducationFragment(): ViewPagerFragment<FragmentEducationBinding>(), FlowCollector<Profile?> {
    private val viewModel: ProfileViewModel by viewModels()
    private val itemsAdapter by lazy { EducationsAdapter() }
    private val activeUID = Firebase.auth.currentUser?.uid
    private val setupMenu by lazy { setupMenu(requireActivity()) }
    private val touchHelper by lazy { itemsAdapter.activateTouchHelper() }
    private lateinit var profile: Profile

    override fun getTabInformation(): TabInfo = TabInfo(
        "Education",
        R.drawable.ic_education_24
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEducationBinding.inflate(inflater, container, false)
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
        lifecycleScope.launch {
            if(viewModel.updateProfile(profile, changes)){
                Toast.makeText(requireContext(), "Educations Saved!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Educations not saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            viewModel.readActiveProfile()?.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)?.let {
                setupRecyclerView()
                it.collect(this@EducationFragment)
            } ?: noActiveProfile()
        }
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

    private fun enableEditing() {
        with(binding.btnAdd){
            isEnabled = true
            visibility = View.VISIBLE
            setOnClickListener {
                EducationEditDialog(requireActivity(), profile).show()
            }
        }
        itemsAdapter.enableEditing(EducationEditDialog(requireActivity(), profile))
    }

    override suspend fun emit(value: Profile?) {
        value?.let {
            profile = it
            itemsAdapter.setList(it.educations)
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