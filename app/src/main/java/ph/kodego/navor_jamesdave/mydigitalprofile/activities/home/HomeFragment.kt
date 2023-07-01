package ph.kodego.navor_jamesdave.mydigitalprofile.activities.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.HomeAction
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
        binding.setupRecyclerView(viewModel.profilePagingData) {
            viewModel.action(HomeAction.View(it))
            toProfile()
        }
        binding.setupSearch {
            viewModel.action(HomeAction.Search(it))
        }
    }

    private fun FragmentHomeBinding.setupRecyclerView(data: Flow<PagingData<Profile>>, view: (Profile) -> Unit){
        val pagingAdapter = ProfilePagingAdapter{ view(it) }
        listProfiles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pagingAdapter
        }
        lifecycleScope.launch {
            data.collectLatest(pagingAdapter::submitData)
        }
        lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collect{monitorListState(it)}
        }
        btnRetry.setOnClickListener { pagingAdapter.retry() }
        btnRefresh.setOnClickListener { pagingAdapter.refresh() }
    }

    private fun FragmentHomeBinding.monitorListState(state: CombinedLoadStates){
        val isListEmpty = state.refresh is LoadState.NotLoading
                && listProfiles.adapter?.itemCount == 0
        val isLoading = state.refresh is LoadState.Loading
                && listProfiles.adapter?.itemCount == 0
        val isError = state.refresh is LoadState.Error

        listProfiles.isVisible = !isListEmpty && !isLoading && !isError
        loadingData.isVisible = isLoading
        layoutRetry.isVisible = isError
        listEmpty.isVisible = isListEmpty

        val errorState = state.refresh as? LoadState.Error
        errorState?.let {
            errorMessage.text = it.error.localizedMessage
        }
    }

    private fun FragmentHomeBinding.setupSearch(search: (String) -> Unit){
        searchBox.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateListFromQuery(search)
                true
            } else {
                false
            }
        }
        searchBox.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateListFromQuery(search)
                true
            } else {
                false
            }
        }
        layoutSearch.setEndIconOnClickListener {
            search("")
            searchBox.text?.clear()
        }
    }

    private fun FragmentHomeBinding.updateListFromQuery(search: (String) -> Unit){
        searchBox.text?.trim().let {
            if (!it.isNullOrEmpty()) {
                listProfiles.scrollToPosition(0)
                search(it.toString())
            }
        }
    }

    private fun toProfile(){
        val intent = Intent(context, ProfileActivity::class.java)
        startActivity(intent)
    }
}