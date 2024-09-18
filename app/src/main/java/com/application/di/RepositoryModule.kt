package com.application.di

import com.application.data.datasource.IProjectService
import com.application.data.repository.ProjectRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideProjectRepository(projectService: IProjectService): ProjectRepository {
        return ProjectRepository(projectService)
    }

}