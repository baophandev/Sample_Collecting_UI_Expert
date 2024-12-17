package com.application.ui.viewmodel

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.R
import com.application.constant.UiStatus
import com.application.data.entity.Comment
import com.application.data.repository.PostRepository
import com.application.ui.state.PostDetailUiState
import com.sc.library.attachment.entity.Attachment
import com.sc.library.utility.state.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val application: Application
) : ViewModel() {

    private val _state = MutableStateFlow(PostDetailUiState())
    val state = _state.asStateFlow()

    fun fetchPost(postId: String, skipCached: Boolean = false) {
        _state.update { it.copy(status = UiStatus.LOADING) }

        viewModelScope.launch(Dispatchers.IO) {
            val post = async {
                when (val rsState = postRepository.getPost(postId, skipCached).last()) {
                    is ResourceState.Error -> null
                    is ResourceState.Success -> rsState.data
                }
            }.await()
            val files = async {
                when (val rsState = postRepository.getFilesInPost(postId, skipCached).last()) {
                    is ResourceState.Success -> rsState.data
                    is ResourceState.Error -> null
                }
            }.await()

            if (post == null || files == null)
                _state.update { it.copy(status = UiStatus.ERROR, error = R.string.get_post_error) }
            else _state.update { it.copy(status = UiStatus.SUCCESS, post = post, files = files) }
        }
    }

    fun startDownload(attachment: Attachment) {
        val downloadManager =
            application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(Uri.parse(attachment.url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, attachment.name)
            .setMimeType(attachment.type)

        downloadManager.enqueue(request)
    }

    fun updateComment(index: Int, fileId: String, newValue: String) {
        updateComment(index, fileId) { comments ->
            comments[fileId]?.copy(content = newValue) ?: Comment(content = newValue)
        }
    }

    fun updateComment(index: Int, fileId: String, attachments: List<Attachment>) {
        updateComment(index, fileId) { comments ->
            comments[fileId]?.copy(attachments = attachments)
                ?: Comment(content = "", attachments = attachments)
        }
    }

    private fun updateComment(
        index: Int,
        fileId: String,
        newComment: (Map<String, Comment>) -> Comment
    ) {
        _state.update { currentState ->
            val comments = currentState.newComments.toMutableMap()
            val newCmt = newComment(comments)
            comments[fileId] = newCmt
            Log.i(TAG, "updateComment: $newCmt")
            // update rendering entity
            val mutFiles = currentState.files.toMutableList()
            val currFile = mutFiles[index]
            mutFiles[index] = currFile.copy(comment = newCmt)

            currentState.copy(newComments = comments, files = mutFiles)
        }
    }

    fun updateGeneralComment(newValue: String) {
        _state.update { currentState ->
            val currentPost = currentState.post
            val newGeneralCmt = currentPost?.generalComment?.copy(content = newValue)
            val newPost = currentPost?.copy(generalComment = newGeneralCmt) // for rendering

            currentState.copy(
                post = newPost,
                newGeneralComment = newGeneralCmt
            )
        }
    }

    fun updateGeneralComment(attachments: List<Attachment>) {
        _state.update { currentState ->
            val currentPost = currentState.post
            val newGeneralCmt = currentPost?.generalComment?.copy(attachments = attachments)
            val newPost = currentPost?.copy(generalComment = newGeneralCmt) // for rendering

            currentState.copy(
                post = newPost,
                newGeneralComment = newGeneralCmt
            )
        }
    }

    fun submit() {
        if (!validate()) {
            _state.update { it.copy(error = R.string.submit_post_error) }
            return
        }

        val currentState = state.value
        val post = currentState.post!!
        _state.update { it.copy(status = UiStatus.LOADING) }

        viewModelScope.launch(Dispatchers.IO) {
            val generalComment = async {
                currentState.newGeneralComment!!.let { comment ->
                    val rsState = postRepository.createGeneralComment(
                        postId = post.id,
                        content = comment.content,
                        attachments = comment.attachments?.map { Uri.parse(it.url) }
                    ).last()
                    when (rsState) {
                        is ResourceState.Success -> rsState.data
                        is ResourceState.Error -> null
                    }
                }
            }.await()
            val comments = currentState.newComments.map { (fileId, comment) ->
                async {
                    val rsState = postRepository.createComment(
                        fileId = fileId,
                        content = comment.content,
                        attachments = comment.attachments?.map { Uri.parse(it.url) }
                    ).last()
                    when (rsState) {
                        is ResourceState.Success -> rsState.data
                        is ResourceState.Error -> null
                    }
                }
            }.awaitAll()

            if (generalComment == null || comments.any { it == null })
                _state.update {
                    it.copy(
                        status = UiStatus.ERROR,
                        error = R.string.create_comment_error
                    )
                }
            else {
                _state.update { PostDetailUiState() }
                fetchPost(postId = post.id, skipCached = true)
            }
        }
    }

    private fun validate(): Boolean {
        val currentState = state.value
        return currentState.newGeneralComment != null || currentState.post == null
    }

    fun gotError() {
        _state.update { it.copy(error = null) }
    }

    companion object {
        const val TAG = "PostDetailViewModel"
    }

}