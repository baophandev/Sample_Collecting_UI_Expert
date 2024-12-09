package com.application.di

import com.application.android.user_library.datasource.IUserService
import com.application.android.user_library.datasource.impl.UserServiceImpl
import com.application.constant.ServiceHost
import com.application.data.datasource.IAttachmentService
import com.application.data.datasource.IProjectService
import com.application.data.datasource.impl.AttachmentServiceImpl
import com.application.data.datasource.impl.ProjectServiceImpl
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
        val baseUrl = "http://${ServiceHost.GATEWAY_SERVER}/api/v1/user/"
        return UserServiceImpl(baseUrl)
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