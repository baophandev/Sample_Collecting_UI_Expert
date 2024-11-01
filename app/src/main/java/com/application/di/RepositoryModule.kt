package com.application.di

import android.content.Context
import com.application.data.datasource.IAttachmentService
import com.application.data.datasource.IProjectService
import com.application.data.datasource.IUserService
import com.application.data.repository.AttachmentRepository
import com.application.data.repository.FormRepository
import com.application.data.repository.ProjectRepository
import com.application.data.repository.StageRepository
import com.application.data.repository.UserRepository
import com.application.util.FileReader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideStageRepository(projectService: IProjectService): StageRepository {
        return StageRepository(projectService)
    }

    @Provides
    @Singleton
    fun provideFormRepository(projectService: IProjectService): FormRepository {
        return FormRepository(projectService)
    }

    @Provides
    @Singleton
    fun provideAttachmentRepository(
        @ApplicationContext context: Context,
        attachmentService: IAttachmentService
    ): AttachmentRepository {
        val fileReader = FileReader(context)
        return AttachmentRepository(fileReader, attachmentService)
    }

}