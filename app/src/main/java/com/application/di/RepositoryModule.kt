package com.application.di

import android.content.Context
import com.application.data.datasource.IPostService
import com.application.data.datasource.IProjectService
import com.application.data.repository.FieldRepository
import com.application.data.repository.FormRepository
import com.application.data.repository.PostRepository
import com.application.data.repository.ProjectRepository
import com.application.data.repository.SampleRepository
import com.application.data.repository.StageRepository
import com.sc.library.attachment.datasource.IAttachmentService
import com.sc.library.attachment.repository.AttachmentRepository
import com.sc.library.user.datasource.IUserService
import com.sc.library.user.repository.UserRepository
import com.sc.library.utility.file.FileReader
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
    fun provideStageRepository(
        projectService: IProjectService,
        userRepository: UserRepository,
    ): StageRepository {
        return StageRepository(projectService, userRepository)
    }

    @Provides
    @Singleton
    fun provideFieldRepository(projectService: IProjectService): FieldRepository {
        return FieldRepository(projectService)
    }

    @Provides
    @Singleton
    fun provideFormRepository(
        projectService: IProjectService,
        fieldRepository: FieldRepository
    ): FormRepository {
        return FormRepository(projectService, fieldRepository)
    }

    @Provides
    @Singleton
    fun provideSampleRepository(
        projectService: IProjectService,
        attachmentRepository: AttachmentRepository,
        fieldRepository: FieldRepository
    ): SampleRepository {
        return SampleRepository(
            projectService = projectService,
            attachmentRepository = attachmentRepository,
            fieldRepository = fieldRepository
        )
    }

    @Provides
    @Singleton
    fun providePostRepository(
        postService: IPostService,
        userRepository: UserRepository,
        attachmentRepository: AttachmentRepository,
    ): PostRepository {
        return PostRepository(
            userRepository = userRepository,
            atmRepository = attachmentRepository,
            service = postService
        )
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