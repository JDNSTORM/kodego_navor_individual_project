package ph.kodego.navor_jamesdave.mydigitalprofile.activities.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.ProfilesAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentHomeBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.loadData
import ph.kodego.navor_jamesdave.mydigitalprofile.extensions.showData
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.ProfileViewModel

@AndroidEntryPoint
class HomeFragment(): ViewPagerFragment<FragmentHomeBinding>(), FlowCollector<List<Profile>> {
    override fun getTabInformation(): TabInfo = TabInfo(
        "Home",
        R.drawable.ic_home_24
    )
    private val viewModel by lazy{
        ViewModelProvider(requireActivity())[ProfileViewModel::class.java]
    }
    private val itemsAdapter by lazy { ProfilesAdapter(viewModel) }
    private var profiles: List<Profile> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        binding.searchBox.addTextChangedListener {
            if (it?.length!! > 3){
                searchList(it.toString())
            }else{
                resetRecyclerView()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.loadData()
        lifecycleScope.launch {
            viewModel.group.collect(this@HomeFragment)
        }
        with(binding.listProfiles) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = itemsAdapter
        }
    }

    private fun resetRecyclerView(){
        itemsAdapter.setList(profiles)
    }

    private fun searchList(keyword: String){
        itemsAdapter.setList(
            profiles.filter { profile ->
                profile.profession.contains(keyword, true) or profile.displayName().contains(keyword, true)
            }
        )
    }

    override suspend fun emit(value: List<Profile>) {
        profiles = value
        resetRecyclerView()
        binding.showData()
    }


}