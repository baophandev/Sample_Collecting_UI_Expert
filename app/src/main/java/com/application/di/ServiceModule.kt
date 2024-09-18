package com.application.di

import com.application.data.datasource.IProjectService
import com.application.data.datasource.impl.ProjectServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    @Singleton
    fun provideProjectService() : IProjectService {
        return ProjectServiceImpl()
    }

}