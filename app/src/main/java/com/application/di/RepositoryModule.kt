package com.application.di

import com.application.data.datasource.IAttachmentService
import com.application.data.datasource.IProjectService
import com.application.data.datasource.IUserService
import com.application.data.repository.AttachmentRepository
import com.application.data.repository.ProjectRepository
import com.application.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userService: IUserService): UserRepository {
        return UserRepository(userService)
    }

    @Provides
    @Singleton
    fun provideProjectRepository(
        projectService: IProjectService,
        userRepository: UserRepository,
        attachmentRepository: AttachmentRepository
    ): ProjectRepository {
        return ProjectRepository(projectService, userRepository, attachmentRepository)
    }

    @Provides
    @Singleton
    fun provideAttachmentRepository(attachmentService: IAttachmentService): AttachmentRepository {
        return AttachmentRepository(attachmentService)
    }

}