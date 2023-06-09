package ph.kodego.navor_jamesdave.mydigitalprofile.viewmodels.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.auth.FirebaseAuthDAOImpl
import ph.kodego.navor_jamesdave.mydigitalprofile.firebase.storage.FirebaseStorageDAOImpl

@Module
@InstallIn(ViewModelComponent::class)
object FirebaseModule {

    @ViewModelScoped
    @Provides
    fun provideAuthDAO(@ApplicationContext context: Context): FirebaseAuthDAOImpl{
        return FirebaseAuthDAOImpl(context)
    }

    @ViewModelScoped
    @Provides
    fun provideStorageDAO(@ApplicationContext context: Context): FirebaseStorageDAOImpl{
        return FirebaseStorageDAOImpl(context)
    }
}