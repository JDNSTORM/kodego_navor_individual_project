package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.components.SingletonComponent
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.AccountDAOImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountModule {

    @Singleton
    @Provides
    fun provideAccountDAO() = AccountDAOImpl()
}