package fr.riverjach.readyset.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import fr.riverjach.readyset.data.local.ReadySetDatabase
import fr.riverjach.readyset.data.local.dao.PackingItemDao
import fr.riverjach.readyset.data.local.dao.TripDao
import fr.riverjach.readyset.data.repository.TripRepositoryImpl
import fr.riverjach.readyset.domain.repository.TripRepository
import fr.riverjach.readyset.domain.usecase.AddPackingItemUseCase
import fr.riverjach.readyset.domain.usecase.CreateTripWithTemplateUseCase
import fr.riverjach.readyset.domain.usecase.DeletePackingItemUseCase
import fr.riverjach.readyset.domain.usecase.DeleteTripUseCase
import fr.riverjach.readyset.domain.usecase.GetTemplatesUseCase
import fr.riverjach.readyset.domain.usecase.ObserveTripDetailUseCase
import fr.riverjach.readyset.domain.usecase.ObserveTripsUseCase
import fr.riverjach.readyset.domain.usecase.ToggleItemPackedUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideReadySetDatabase(
        @ApplicationContext context: Context
    ): ReadySetDatabase {
        return Room.databaseBuilder(
            context,
            ReadySetDatabase::class.java,
            "readyset.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTripDao(database: ReadySetDatabase): TripDao = database.tripDao()

    @Provides
    fun providePackingItemDao(database: ReadySetDatabase): PackingItemDao = database.packingItemDao()
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTripRepository(
        tripDao: TripDao,
        packingItemDao: PackingItemDao
    ): TripRepository {
        return TripRepositoryImpl(tripDao, packingItemDao)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideObserveTripsUseCase(repository: TripRepository) = ObserveTripsUseCase(repository)

    @Provides
    fun provideObserveTripDetailUseCase(repository: TripRepository) = ObserveTripDetailUseCase(repository)

    @Provides
    fun provideGetTemplatesUseCase(repository: TripRepository) = GetTemplatesUseCase(repository)

    @Provides
    fun provideCreateTripWithTemplateUseCase(repository: TripRepository) = CreateTripWithTemplateUseCase(repository)

    @Provides
    fun provideToggleItemPackedUseCase(repository: TripRepository) = ToggleItemPackedUseCase(repository)

    @Provides
    fun provideAddPackingItemUseCase(repository: TripRepository) = AddPackingItemUseCase(repository)

    @Provides
    fun provideDeleteTripUseCase(repository: TripRepository) = DeleteTripUseCase(repository)

    @Provides
    fun provideDeletePackingItemUseCase(repository: TripRepository) = DeletePackingItemUseCase(repository)
}
