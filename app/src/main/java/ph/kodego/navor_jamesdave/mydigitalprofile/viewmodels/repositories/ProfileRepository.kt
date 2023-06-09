package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories

import dagger.hilt.android.scopes.ViewModelScoped
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources.AccountDataSource
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources.ProfileDataSource
import javax.inject.Inject

@ViewModelScoped
class ProfileRepository @Inject constructor(
    val profileSource: ProfileDataSource,
    val accountSource: AccountDataSource
) {
}