package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.HomeAction
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.ProfileRepository
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProfileRepository
): ViewModel() {
    val profilePagingData: Flow<PagingData<Profile>>
    val action: (HomeAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<HomeAction>()
        val searchAction = actionStateFlow
            .filterIsInstance<HomeAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(HomeAction.Search("")) }

        profilePagingData = searchAction.flatMapLatest { //TODO: SearchQuery
            repository.getProfileStream().cachedIn(viewModelScope)
        }

        action = {
            when(it){
                is HomeAction.View -> viewProfile(it.profile)
                else -> viewModelScope.launch { actionStateFlow.emit(it) }
            }
        }
    }

    private fun viewProfile(profile: Profile?) = viewModelScope.launch {
        profile?.let {
            repository.profileSource.awaitProfile()
            if (it.displayName().isEmpty()){
                val account = repository.accountSource.getAccount(it.refUID)
                account?.let { account ->
                    it.setAccount(account)
                }
            }
            repository.profileSource.viewProfile(it)
        } ?: repository.profileSource.clearProfile()
    }
}