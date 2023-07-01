package ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.career

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
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.CareersAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadData
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class CareerFragment(): ViewPagerFragment<FragmentCareerBinding>(), FlowCollector<Profile?> {
    private val viewModel: ProfileViewModel by viewModels()
    private val itemsAdapter = CareersAdapter()
    override fun getTabInformation(): TabInfo = TabInfo(
        "Career",
        R.drawable.ic_work_history_24
    )
    private val activeUID = Firebase.auth.currentUser?.uid
    private val setupMenu by lazy {setupMenu(requireActivity())}
    private val touchHelper by lazy { itemsAdapter.activateTouchHelper() }
    private lateinit var profile: Profile

    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentCareerBinding {
        return FragmentCareerBinding.inflate(inflater, container, false)
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
                            touchHelper.attachToRecyclerView(binding.listCareer)
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

    private fun loadProfile() {
        binding.loadData()
        lifecycleScope.launch {
            viewModel.readActiveProfile()?.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)?.let {
                setupRecyclerView()
                it.collect(this@CareerFragment)
            } ?: noActiveProfile()
        }
    }

    private fun setupRecyclerView() {
        with(binding.listCareer){
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
                CareerEditDialog(requireActivity(), profile).show()
            }
        }
        itemsAdapter.enableEditing(CareerEditDialog(requireActivity(), profile))
    }

    private fun noActiveProfile() {
        Toast.makeText(requireContext(), "No Profile Selected!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    override suspend fun emit(value: Profile?) {
        value?.let {
            profile = it
            if (it.careers.isNotEmpty()) {
                itemsAdapter.setList(it.careers)
            }else{
                itemsAdapter.setList(emptyList())
            }
            if (it.refUID == activeUID) {
                enableEditing()
                setupMenu
            }
        } ?: noActiveProfile()
    }

    private fun saveList(){
//        val careers = itemsAdapter.careers()
//        careers.lastIndex
//        val changes: Map<String, Any?> = mapOf(Profile.KEY_CAREERS to careers)
//        lifecycleScope.launch {
//            if(viewModel.updateProfile(profile, changes)){
//                Toast.makeText(requireContext(), "Careers Saved!", Toast.LENGTH_SHORT).show()
//            }else{
//                Toast.makeText(requireContext(), "Careers not saved", Toast.LENGTH_SHORT).show()
//            }
//        }
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