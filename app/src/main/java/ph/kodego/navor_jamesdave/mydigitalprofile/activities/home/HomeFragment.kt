package ph.kodego.navor_jamesdave.mydigitalprofile.activities.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.R
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ViewPagerFragment
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.profile.ProfileActivity
import ph.kodego.navor_jamesdave.mydigitalprofile.adapters.recyclerview.ProfilePagingAdapter
import ph.kodego.navor_jamesdave.mydigitalprofile.databinding.FragmentHomeBinding
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.models.TabInfo
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.HomeViewModel

@AndroidEntryPoint
class HomeFragment(): ViewPagerFragment<FragmentHomeBinding>(){
    override fun inflateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun getTabInformation(): TabInfo = TabInfo(
        "Home",
        R.drawable.ic_home_24
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel: HomeViewModel by viewModels()
        binding.setupRecyclerView(viewModel.profilePagingData, {})
    }

    private fun FragmentHomeBinding.setupRecyclerView(data: Flow<PagingData<Profile>>, view: (Profile) -> Unit){
        val pagingAdapter = ProfilePagingAdapter{
            view(it)
//            toProfile() //TODO
        }
        listProfiles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pagingAdapter
        }
        lifecycleScope.launch {
            data.flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collectLatest(pagingAdapter::submitData)
        }
        lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collect{monitorListState(it)}
        }
    }

    private fun FragmentHomeBinding.monitorListState(state: CombinedLoadStates){
        val isListEmpty = state.refresh is LoadState.NotLoading
                && listProfiles.adapter?.itemCount == 0
        val isLoading = state.refresh is LoadState.Loading
                && listProfiles.adapter?.itemCount == 0
        val isError = state.refresh is LoadState.Error

        listProfiles.isVisible = !isListEmpty && !isLoading && !isError
        loadingData.isVisible = isLoading
    }

    private fun resetRecyclerView(){
//        itemsAdapter.setList(profiles)
    }

    private fun toProfile(){
        val intent = Intent(context, ProfileActivity::class.java)
        startActivity(intent)
    }

}