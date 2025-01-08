package com.application.data.paging

import com.application.constant.ProjectQueryType
import com.application.constant.ProjectStatus
import com.application.data.entity.Project
import com.application.data.repository.ProjectRepository
import io.github.nhatbangle.sc.user.repository.UserRepository

class ProjectPagingSource(
    private val query: ProjectQueryType,
    private val status: ProjectStatus,
    private val projectRepository: ProjectRepository,
    private val userRepository: UserRepository
) : AbstractPagingSource<Project>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Project> {
        val nextPageNumber = params.key ?: 0
        val loggedUser = userRepository.loggedUser
            ?: return LoadResult.Error(Exception("Cannot get logged user data."))

        val result = projectRepository.getAllProjects(
            userId = loggedUser.id,
            query = query,
            status = status,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )
        return retrievePagingForward(nextPageNumber, result)
    }

    companion object {
        const val TAG = "ProjectPagingSource"
    }

}