package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.firestore.ProfileDAOImpl

@Module
@InstallIn(ViewModelComponent::class)
object ProfileModule {

    @ViewModelScoped
    @Provides
    fun provideProfileDAO(): ProfileDAOImpl = ProfileDAOImpl()
}