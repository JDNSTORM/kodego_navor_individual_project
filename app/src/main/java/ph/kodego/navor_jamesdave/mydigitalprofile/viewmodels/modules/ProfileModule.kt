package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.ProfileDAOImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Singleton
    @Provides
    fun provideProfileDAO(): ProfileDAOImpl = ProfileDAOImpl()
}