package com.application.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.application.data.datasource.IProjectService
import com.application.data.entity.Form

class FieldRepository(
    private val projectService: IProjectService,
) : PagingSource<Int, Form>() {
    private val cachedForms: MutableMap<String, Form> = mutableMapOf()

    override fun getRefreshKey(state: PagingState<Int, Form>): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Form> {
        TODO("Not yet implemented")
    }

    //Field
    /**
     * Create a new field of project.
     * @param .
     */

}