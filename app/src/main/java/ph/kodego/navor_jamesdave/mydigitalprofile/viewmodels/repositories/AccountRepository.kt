package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories

import dagger.hilt.android.scopes.ViewModelScoped
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.auth.FirebaseAuthDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.repositories.data_sources.AccountDataSource
import javax.inject.Inject

@ViewModelScoped
class AccountRepository @Inject constructor(val auth: FirebaseAuthDAOImpl,val source: AccountDataSource) {
}