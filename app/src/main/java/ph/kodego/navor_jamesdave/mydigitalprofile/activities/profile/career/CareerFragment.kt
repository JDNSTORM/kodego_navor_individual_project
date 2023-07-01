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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ListMenu
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ProfileAction
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ViewedProfileState
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.CareersAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentCareerBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadData
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class CareerFragment(): ViewPagerFragment<FragmentCareerBinding>() {
    private val viewModel: ProfileViewModel by viewModels()
    private val itemsAdapter = CareersAdapter()
    override fun getTabInformation(): TabInfo = TabInfo(
        "Career",
        R.drawable.ic_work_history_24
    )
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

        binding.setupUI(
            viewModel.viewedProfileState,
            viewModel.accountState,
            viewModel.action
        )
    }

    private fun FragmentCareerBinding.setupUI(
        state: StateFlow<ViewedProfileState>,
        accountState: StateFlow<AccountState>,
        action: (ProfileAction) -> StateFlow<RemoteState>?
    ) {
        loadData()
        setupRecyclerView()
        val (flow, uid) = (state.value as ViewedProfileState.Active)
        val activeUID = (accountState.value as? AccountState.Active)?.uid

        lifecycleScope.launch {
            flow.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{
                it?.let {
                    profile = it
                    if (it.careers.isNotEmpty()) {
                        itemsAdapter.setList(it.careers)
                    }else{
                        itemsAdapter.setList(emptyList())
                    }
                } ?: noActiveProfile()
            }
        }

        if (uid == activeUID){
            enableEditing{
                val remoteState = action(ProfileAction.Update(profile, it))!!
                monitorState(remoteState)
            }
        }
    }

    private fun monitorState(state: StateFlow<RemoteState>){
        lifecycleScope.launch {
            state.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED).collect{
                when(it){
                    RemoteState.Success -> Toast.makeText(context, "Careers Saved!", Toast.LENGTH_SHORT).show()
                    RemoteState.Failed -> Toast.makeText(context, "Careers not saved", Toast.LENGTH_SHORT).show()
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

    private fun FragmentCareerBinding.setupRecyclerView() {
        with(listCareer){
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemsAdapter
        }
        showData()
    }

    private fun FragmentCareerBinding.enableEditing(update: (Map<String, Any?>) -> Unit) {
        setupMenu
        with(btnAdd){
            isEnabled = true
            visibility = View.VISIBLE
            setOnClickListener {
                CareerEditDialog(requireActivity(), profile, update).show()
            }
        }
        itemsAdapter.enableEditing(CareerEditDialog(requireActivity(), profile, update))
    }

    private fun noActiveProfile() {
        Toast.makeText(requireContext(), "No Profile Selected!", Toast.LENGTH_SHORT).show()
        requireActivity().finish()
    }

    private fun saveList(){
        val careers = itemsAdapter.careers()
        careers.lastIndex
        val changes: Map<String, Any?> = mapOf(Profile.KEY_CAREERS to careers)
        val remoteState = viewModel.action(ProfileAction.Update(profile, changes))!!
        monitorState(remoteState)
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