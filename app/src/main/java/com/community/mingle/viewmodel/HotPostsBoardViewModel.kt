package com.community.mingle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.model.post.HomeHotPost
import com.community.mingle.model.post.PostType
import com.community.mingle.model.post.TotalBoardType
import com.community.mingle.service.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotPostsBoardViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    //best post list
    private val _bestPostList = MutableStateFlow<List<HomeHotPost>>(emptyList())
    private val lastUnivBestPostId = _bestPostList.map { list -> list.lastOrNull { it.postType is PostType.Univ }?.postId }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    private val lastTotalBestPostId = _bestPostList.map { list -> list.lastOrNull { it.postType is PostType.Total }?.postId }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    val bestPostLIst = _bestPostList.asStateFlow()

    init {
        loadAllUniteBestPost(lastTotalPostId = 0, lastUnivPostId = 0)

    }

    fun refreshUniteBestPost() {
        loadAllUniteBestPost(lastTotalPostId = 0, lastUnivPostId = 0)
    }

    private fun loadAllUniteBestPost(
        lastTotalPostId: Int,
        lastUnivPostId: Int,
    ) {
        viewModelScope.launch {
            postRepository.getUniteBestPostList(
                lastTotalPost = lastTotalPostId,
                lastUnivPost = lastUnivPostId,
            ).catch {
                _loading.value = false
                //show error page?
            }.collect { list ->
                _bestPostList.value = list
                _loading.value = false
            }
        }
    }
}