package com.community.mingle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.model.post.PostType
import com.community.mingle.service.models.PostResult
import com.community.mingle.service.repository.UnivTotalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotPostsBoardViewModel @Inject constructor(
    private val univTotalRepository: UnivTotalRepository,
) : ViewModel() {

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()
    private val _swipeLoading = MutableStateFlow(false)
    val swipeLoading = _swipeLoading.asStateFlow()
    private val _bestPostList = MutableStateFlow<List<PostResult>>(emptyList())
    private var lastTotalBestPostId = Int.MAX_VALUE
    private var lastUnivBestPostId = Int.MAX_VALUE
    val bestPostList = _bestPostList.asStateFlow()
    private var _lastPostId: Int = -1
    val lastPostId: Int
        get() = _lastPostId

    init {
        loadNextHotPostList()
    }

    fun swipeRefreshHotPostList() {
        viewModelScope.launch {
            _swipeLoading.value = true
            lastTotalBestPostId = Int.MAX_VALUE
            lastUnivBestPostId = Int.MAX_VALUE
            loadHotPostList {
                _bestPostList.value = it
            }
            _swipeLoading.value = false
        }

    }

    fun loadNextHotPostList() {
        viewModelScope.launch {
            _loading.value = true
            loadHotPostList()
            _loading.value = false
        }
    }

    private suspend fun loadHotPostList(onSuccess: (List<PostResult>) -> Unit = { _bestPostList.update { prev -> prev + it}}) {
        univTotalRepository.getHotPostList(
            lastUnivBestPostId = lastUnivBestPostId,
            lastTotalBestPostId = lastTotalBestPostId,
        ).onSuccess { newList ->
            onSuccess(newList)
            updateLastPostIds()
            _lastPostId = newList.last().postId
        }
    }

    private fun updateLastPostIds() {
        _bestPostList.value.let { list ->
            lastUnivBestPostId = list.lastOrNull { it.boardType == PostType.Univ }?.postId ?: Int.MAX_VALUE
            lastTotalBestPostId = list.lastOrNull { it.boardType == PostType.Total }?.postId ?: Int.MAX_VALUE
        }
    }
}