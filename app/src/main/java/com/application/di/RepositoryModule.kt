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
import com.sc.library.chat.data.datasource.IChatService
import com.sc.library.chat.data.repository.ConversationRepository
import com.sc.library.chat.data.repository.MessageRepository
import com.sc.library.chat.data.repository.ParticipantRepository
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
    ): ProjectRepository = ProjectRepository(
        projectService = projectService,
        userRepository = userRepository,
        attachmentRepository = attachmentRepository
    )

    @Provides
    @Singleton
    fun provideStageRepository(
        projectService: IProjectService,
        userRepository: UserRepository,
    ): StageRepository = StageRepository(
        projectService = projectService,
        userRepository = userRepository
    )

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
    ): FormRepository = FormRepository(
        projectService = projectService,
        fieldRepository = fieldRepository
    )

    @Provides
    @Singleton
    fun provideSampleRepository(
        projectService: IProjectService,
        attachmentRepository: AttachmentRepository,
        fieldRepository: FieldRepository
    ): SampleRepository = SampleRepository(
        projectService = projectService,
        attachmentRepository = attachmentRepository,
        fieldRepository = fieldRepository
    )

    @Provides
    @Singleton
    fun providePostRepository(
        postService: IPostService,
        userRepository: UserRepository,
        attachmentRepository: AttachmentRepository,
    ): PostRepository = PostRepository(
        userRepository = userRepository,
        atmRepository = attachmentRepository,
        service = postService
    )

    @Provides
    @Singleton
    fun provideAttachmentRepository(
        @ApplicationContext context: Context,
        attachmentService: IAttachmentService
    ): AttachmentRepository {
        val fileReader = FileReader(context)
        return AttachmentRepository(fileReader, attachmentService)
    }

    @Provides
    @Singleton
    fun provideConversationRepository(
        chatService: IChatService,
        userRepository: UserRepository,
    ): ConversationRepository = ConversationRepository(
        chatService = chatService,
        userRepository = userRepository,
    )

    @Provides
    @Singleton
    fun provideMessageRepository(
        chatService: IChatService,
        attachmentRepository: AttachmentRepository,
        userRepository: UserRepository,
    ): MessageRepository = MessageRepository(
        chatService = chatService,
        attachmentRepository = attachmentRepository,
        userRepository = userRepository,
    )

    @Provides
    @Singleton
    fun provideParticipantRepository(
        chatService: IChatService,
        conversationRepository: ConversationRepository,
        userRepository: UserRepository,
    ): ParticipantRepository = ParticipantRepository(
        chatService = chatService,
        conversationRepository = conversationRepository,
        userRepository = userRepository,
    )

}