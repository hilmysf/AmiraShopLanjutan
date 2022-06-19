package hilmysf.amirashoplanjutan.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hilmysf.amirashoplanjutan.data.repositories.ProductsRepository
import hilmysf.amirashoplanjutan.data.source.firebase.FirebaseSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFirebaseSource(): FirebaseSource = FirebaseSource()

    @Singleton
    @Provides
    fun provideProductsRepository(firebaseSource: FirebaseSource): ProductsRepository =
        ProductsRepository(firebaseSource)
}