package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.AccountDAOImpl

@Module
@InstallIn(ActivityRetainedComponent::class)
object AccountModule {

    @ActivityRetainedScoped
    @Provides
    fun provideAccountDAO() = AccountDAOImpl()
}