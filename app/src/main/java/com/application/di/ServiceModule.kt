package com.application.di

import com.application.data.datasource.IAttachmentService
import com.application.data.datasource.IProjectService
import com.application.data.datasource.IUserService
import com.application.data.datasource.impl.AttachmentServiceImpl
import com.application.data.datasource.impl.ProjectServiceImpl
import com.application.data.datasource.impl.UserServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideUserService() : IUserService {
        return UserServiceImpl()
    }

    @Provides
    @Singleton
    fun provideProjectService() : IProjectService {
        return ProjectServiceImpl()
    }

    @Provides
    @Singleton
    fun provideAttachmentService() : IAttachmentService {
        return AttachmentServiceImpl()
    }

}