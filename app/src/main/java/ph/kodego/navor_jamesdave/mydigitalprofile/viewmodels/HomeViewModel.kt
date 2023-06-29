package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.home.HomeAction
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    repository: ProfileRepository
): ViewModel() {
    val profilePagingData = repository.getProfileStream().cachedIn(viewModelScope)
    val action: (HomeAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<HomeAction>()
        val searchAction = actionStateFlow
            .filterIsInstance<HomeAction.Search>()
            .distinctUntilChanged()
        val viewAction = actionStateFlow
            .filterIsInstance<HomeAction.View>()
            .distinctUntilChanged()


        action = { viewModelScope.launch { actionStateFlow.emit(it) } }
    }
}