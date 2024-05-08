package com.example.projektmunka.di

import android.content.Context
import com.example.firstapp.repository.UserDataRepository
import com.example.projektmunka.logic.UserRouteTracker
import com.example.projektmunka.dataremote.AuthDao
import com.example.projektmunka.logic.FitnessCalculator
import com.example.projektmunka.logic.GPXFileHandler
import com.example.projektmunka.logic.KMLFileHandler
import com.example.projektmunka.logic.RouteGenerator.CaloriesDiffRouteGenerator
import com.example.projektmunka.logic.RouteGenerator.CircularDiffRouteGenerator
import com.example.projektmunka.logic.RouteGenerator.DiffRouteGenerator
import com.example.projektmunka.logic.RouteGenerator.TimeDiffRouteGenerator
import com.example.projektmunka.logic.StepCounter
import com.example.projektmunka.remote.UserDataDao
import com.example.projektmunka.remote.UserLocationDao
import com.example.projektmunka.remote.UserRouteDao
import com.example.projektmunka.remote.UserRouteTrackerDao
import com.example.projektmunka.repository.AuthRepository
import com.example.projektmunka.repository.NearbyUsersRepository
import com.example.projektmunka.repository.UserLocationRepository
import com.example.projektmunka.logic.LocationTracker
import com.example.projektmunka.repository.UserRouteRepository
import com.example.projektmunka.repository.UserRouteTrackerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun provideAuthDao() = AuthDao()
    @Provides
    @Singleton
    fun provideUserDataDao() = UserDataDao()
    @Provides
    @Singleton
    fun provideUserLocationDao() = UserLocationDao()
    @Provides
    @Singleton
    fun provideUserRouteDao() = UserRouteDao()
    @Provides
    @Singleton
    fun provideUserRouteTrackerDao() = UserRouteTrackerDao()
    @Provides
    @Singleton
    fun provideStepCounter(@ApplicationContext context: Context) = StepCounter(context)

    @Provides
    @Singleton
    fun provideKMLFileHandler(@ApplicationContext context: Context) = KMLFileHandler(context)

    @Provides
    @Singleton
    fun provideGPXFileHandler(@ApplicationContext context: Context) = GPXFileHandler(context)

    @Provides
    @Singleton
    fun provideLocationTracker(@ApplicationContext context: Context) = LocationTracker(context)
    @Provides
    @Singleton
    fun provideUserRouteTracker(locationTracker: LocationTracker, fitnessCalculator: FitnessCalculator, stepCounter: StepCounter)
    = UserRouteTracker(locationTracker, fitnessCalculator, stepCounter)

    @Provides
    @Singleton
    fun provideCircularDiffRouteGenerator(userRouteTracker: UserRouteTracker, gpxFileHandler: GPXFileHandler, kmlFileHandler: KMLFileHandler) = CircularDiffRouteGenerator(userRouteTracker,
        gpxFileHandler, kmlFileHandler)

    @Provides
    @Singleton
    fun provideDiffRouteGenerator(userRouteTracker: UserRouteTracker) = DiffRouteGenerator(userRouteTracker)

    @Provides
    @Singleton
    fun provideTimeDiffRouteGenerator(userRouteTracker: UserRouteTracker) = TimeDiffRouteGenerator(userRouteTracker)

    @Provides
    @Singleton
    fun provideCaloriesDiffRouteGenerator(userRouteTracker: UserRouteTracker) = CaloriesDiffRouteGenerator(userRouteTracker)

    @Provides
    @Singleton
    fun provideAuthRepository(authDao:AuthDao, userDataRepository: UserDataRepository) = AuthRepository(authDao, userDataRepository)
    @Provides
    @Singleton
    fun provideUserDataRepository(userDataDao: UserDataDao, userLocationDao: UserLocationDao) = UserDataRepository(userDataDao, userLocationDao)
    @Provides
    @Singleton
    fun provideUserLocationRepository(userLocationDao: UserLocationDao) = UserLocationRepository(userLocationDao)

    @Provides
    @Singleton
    fun provideUserRouteRepository(userRouteDao: UserRouteDao) = UserRouteRepository(userRouteDao)

    @Provides
    @Singleton
    fun provideUserRouteTrackerRepository(userRouteTrackerDao: UserRouteTrackerDao) = UserRouteTrackerRepository(userRouteTrackerDao)

    @Provides
    @Singleton
    fun provideNearbyUsersRepository(userDataDao: UserDataDao, userLocationDao: UserLocationDao) = NearbyUsersRepository(userDataDao, userLocationDao)
}
