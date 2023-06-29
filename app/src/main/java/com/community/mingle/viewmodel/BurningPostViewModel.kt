package com.community.mingle.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.model.HotPost
import com.community.mingle.model.post.PostType
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
class BurningPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    //best post list
    private val _bestPostList = MutableStateFlow<List<HotPost>>(emptyList())
    private val lastUnivBestPostId = _bestPostList.map { list -> list.lastOrNull { it.postType is PostType.Univ }?.postId }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    private val lastTotalBestPostId = _bestPostList.map { list -> list.lastOrNull { it.postType is PostType.Total }?.postId }
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    val bestPostLIst = _bestPostList.asStateFlow()

    init {
        loadAllUniteBestPost()
    }

    private fun loadAllUniteBestPost() {
        viewModelScope.launch {
            postRepository.getUniteBestPostList(
                lastTotalPost = lastTotalBestPostId.value ?: 0,
                lastUnivPost = lastUnivBestPostId.value ?: 0,
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