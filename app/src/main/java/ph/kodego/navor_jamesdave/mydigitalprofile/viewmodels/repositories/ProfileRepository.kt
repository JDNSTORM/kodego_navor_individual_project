package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.launch
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.AccountState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.RemoteState
import ph.kodego.navor_jamesdave.mydigitalprofile.activities.ui_models.ViewedProfileState
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.models.Profile
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources.AccountDataSource
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources.ProfileDataSource
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources.ProfilePagingSource
import javax.inject.Inject

@ViewModelScoped
class ProfileRepository @Inject constructor(
    val profileSource: ProfileDataSource,
    val accountSource: AccountDataSource
) {
    fun getProfileStream(): Flow<PagingData<Profile>>{
        val pagingConfig = PagingConfig(
            pageSize = 5,
            enablePlaceholders = false
        )
        val pagingSourceFactory = {ProfilePagingSource(
            {document: DocumentSnapshot?, limit: Int ->
                profileSource.getPublicProfiles(document, limit)
            },
            { accountSource.getAccount(it)!!}
        )}

        return Pager(
            pagingConfig,
            null,
            pagingSourceFactory
        ).flow
    }

    fun getAccountProfiles(): Flow<List<Profile>>?{
        val account = (accountSource.accountState.value as? AccountState.Active)?.account

        return account?.let { flow ->
            profileSource.readProfiles().combine(flow){ profiles, account ->
                profiles.forEach { it.setAccount(account!!) }
                profiles
            }
        }
    }

    fun addProfile(profession: String): StateFlow<RemoteState> {
        val state = MutableStateFlow(RemoteState.Waiting)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val uid = (accountSource.accountState.value as? AccountState.Active)?.uid
                uid?.let {
                    val profile = Profile(it, profession)
                    profileSource.addProfile(profile)
                    state.emit(RemoteState.Success)
                } ?: state.emit(RemoteState.Invalid)
            } catch (e: Exception){
                state.emit(RemoteState.Failed)
            }
            delay(100)
            state.emit(RemoteState.Idle)
        }
        return state.asStateFlow()
    }
}