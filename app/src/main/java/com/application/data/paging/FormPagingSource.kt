package com.application.data.paging

import com.application.data.entity.Form
import com.application.data.repository.FormRepository

class FormPagingSource(
    private val projectId: String,
    private val formRepository: FormRepository
) : AbstractPagingSource<Form>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Form> {
        val nextPageNumber = params.key ?: 0
        val result = formRepository.getAllForms(
            projectId = projectId,
            pageNumber = nextPageNumber,
            pageSize = params.loadSize
        )
        return retrievePagingForward(nextPageNumber, result)
    }

    companion object {
        const val TAG = "FormPagingSource"
    }

}