package com.example.todoappshmr.di.module

import android.content.Context
import com.example.todoappshmr.data.datasource.Room.AppDatabase
import com.example.todoappshmr.data.datasource.Room.DatabaseProvider
import com.example.todoappshmr.data.datasource.Room.TaskLocalDataSource
import com.example.todoappshmr.data.datasource.network.TaskRemoteDataSource
import com.example.todoappshmr.data.repository.network.ApiService
import com.example.todoappshmr.domain.usecases.NetworkUseCase
import com.example.todoappshmr.domain.usecases.TaskUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return DatabaseProvider.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTaskLocalDataSource(database: AppDatabase): TaskLocalDataSource {
        return TaskLocalDataSource(database)
    }

    @Provides
    @Singleton
    fun provideTaskRemoteDataSource(apiService: ApiService): TaskRemoteDataSource {
        return TaskRemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(
        localDataSource: TaskLocalDataSource,
        remoteDataSource: TaskRemoteDataSource,
        networkRepository: NetworkUseCase,
        context: Context
    ): TaskUseCase {
        return TaskUseCase(localDataSource, remoteDataSource, networkRepository, context)
    }
}