package com.application.di

import com.application.constant.ServiceHost
import com.application.data.datasource.IPostService
import com.application.data.datasource.IProjectService
import com.application.data.datasource.impl.PostServiceImpl
import com.application.data.datasource.impl.ProjectServiceImpl
import com.sc.library.attachment.datasource.IAttachmentService
import com.sc.library.attachment.datasource.impl.AttachmentServiceImpl
import com.sc.library.user.datasource.IUserService
import com.sc.library.user.datasource.impl.UserServiceImpl
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
    fun providePostService() : IPostService {
        val baseUrl = "http://${ServiceHost.GATEWAY_SERVER}/api/post/"
        return PostServiceImpl(baseUrl)
    }

    @Provides
    @Singleton
    fun provideAttachmentService() : IAttachmentService {
        val baseUrl = "http://${ServiceHost.GATEWAY_SERVER}/api/file/"
        return AttachmentServiceImpl(baseUrl)
    }

}