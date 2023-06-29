package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
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
}