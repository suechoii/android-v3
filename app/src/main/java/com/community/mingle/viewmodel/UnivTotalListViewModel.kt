package com.community.mingle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.service.models.PostListItem
import com.community.mingle.service.models.PostResult
import com.community.mingle.service.repository.UnivTotalRepository
import com.community.mingle.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnivTotalListViewModel
@Inject
constructor(
    private val repository: UnivTotalRepository,
) : ViewModel() {

    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading
    private val _nextListLoading = MutableStateFlow(false)
    val nextListLoading = _nextListLoading.asStateFlow()

    /* 잔디밭 / 광장 게시물 리스트 불러오기 */
    private val _univTotalList1 = MutableLiveData<List<PostListItem>>()
    val univTotalList1: LiveData<List<PostListItem>> get() = _univTotalList1
    private val _univTotalList2 = MutableLiveData<List<PostListItem>>()
    val univTotalList2: LiveData<List<PostListItem>> get() = _univTotalList2
    private val _univTotalList3 = MutableLiveData<List<PostListItem>>()
    private val _univTotalList4 = MutableLiveData<List<PostListItem>>()
    val univTotalList4: LiveData<List<PostListItem>> get() = _univTotalList4
    private val _lastPostId1 = MutableLiveData<Int>()
    val lastPostId1: LiveData<Int> get() = _lastPostId1
    private val _lastPostId2 = MutableLiveData<Int>()
    val lastPostId2: LiveData<Int> get() = _lastPostId2
    private val _lastPostId3 = MutableLiveData<Int>()
    private val _lastPostId4 = MutableLiveData<Int>()
    val lastPostId4: LiveData<Int> get() = _lastPostId4
    private val _newUnivTotalList1 = MutableLiveData<List<PostListItem>>()
    val newUnivTotalList1: LiveData<List<PostListItem>> get() = _newUnivTotalList1
    private val _newUnivTotalList2 = MutableLiveData<List<PostListItem>>()
    val newUnivTotalList2: LiveData<List<PostListItem>> get() = _newUnivTotalList2
    private val _newUnivTotalList4 = MutableLiveData<List<PostListItem>>()
    val newUnivTotalList4: LiveData<List<PostListItem>> get() = _newUnivTotalList4
    private val _univAllList = MutableStateFlow<List<PostListItem>>(emptyList())
    val univAllList = _univAllList.asStateFlow()
    private val _totalAllList = MutableStateFlow<List<PostListItem>>(emptyList())
    val totalAllList = _totalAllList.asStateFlow()
    private var univAllLastCalledPostId: Int = Int.MAX_VALUE
    private var univFreeLastCalledPostId: Int = Int.MAX_VALUE
    private var univQuestionLastCalledPostId: Int = Int.MAX_VALUE
    private var univCouncilLastCalledPostId: Int = Int.MAX_VALUE
    private var totalAllLastCalledPostId: Int = Int.MAX_VALUE
    private var totalFreeLastCalledPostId: Int = Int.MAX_VALUE
    private var totalQuestionLastCalledPostId: Int = Int.MAX_VALUE
    private var totalMingleLastCalledPostId: Int = Int.MAX_VALUE
    private var loadNextAllUnivJob: Job? = null

    //    fun updateList(postList: Array<PostResult>) {
    //        _newUnivTotalList.postValue(postList.toList())
    //    }
    // isRefreshing 추후에 추가
    fun getUnivList(category: Int, isRefreshing: Boolean) {
        if (isRefreshing)
            _loading.postValue(Event(true))
        when (category) {
            1 -> {
                univFreeLastCalledPostId = Int.MAX_VALUE
            }

            2 -> {
                univQuestionLastCalledPostId = Int.MAX_VALUE
            }

            5 -> {
                univCouncilLastCalledPostId = Int.MAX_VALUE
            }
        }
        viewModelScope.launch {
            repository.getUnivPost(category, Int.MAX_VALUE)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))
                        if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                            when (category) {
                                1 -> {
                                    _univTotalList1.postValue(response.body()!!.result.postListDTO)
                                    val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                    _lastPostId1.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                                }

                                2 -> {
                                    _univTotalList2.postValue(response.body()!!.result.postListDTO)
                                    val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                    _lastPostId2.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                                }

                                3 -> {
                                    _univTotalList3.postValue(response.body()!!.result.postListDTO)
                                    val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                    _lastPostId3.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                                }

                                5 -> {
                                    _univTotalList4.postValue(response.body()!!.result.postListDTO)
                                    val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                    _lastPostId4.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                                }
                            }

                            Log.d("tag_success", response.body().toString())
                        } else if (response.body()!!.code == 3031) {
                            when (category) {
                                1 -> _univTotalList1.postValue(emptyList())
                                2 -> _univTotalList2.postValue(emptyList())
                                3 -> _univTotalList3.postValue(emptyList())
                                5 -> _univTotalList4.postValue(emptyList())
                            }
                        }
                    } else {
                        Log.d("tag_fail", "getUnivList Error: ${response.code()}")
                    }
                }.onFailure {
                    when (category) {
                        1 -> _univTotalList1.postValue(emptyList())
                        2 -> _univTotalList2.postValue(emptyList())
                        3 -> _univTotalList3.postValue(emptyList())
                        5 -> _univTotalList4.postValue(emptyList())
                    }
                }
        }
    }

    fun loadNewAllUnivPosts() {
        viewModelScope.launch {
            univAllLastCalledPostId = Int.MAX_VALUE
            _loading.postValue(Event(true))
            repository.getAllUnivPostList(Int.MAX_VALUE)
                .onSuccess { list -> _univAllList.value = list.sortedByDescending { it.postId } }
                .onFailure { _univAllList.value = emptyList() }
            _loading.postValue(Event(false))
        }
    }

    fun loadNextAllUnivPosts() {
        viewModelScope.launch {
            _loading.postValue(Event(true))
            val lastPostId = (_univAllList.value.lastOrNull { it is PostResult } as? PostResult)?.postId ?: Int.MAX_VALUE
            if (univAllLastCalledPostId > lastPostId) {
                val t = univAllLastCalledPostId
                univAllLastCalledPostId = lastPostId
                _univAllList.update { it.plus(PostListItem.Loading) }
                repository.getAllUnivPostList(lastPostId)
                    .onSuccess { list ->
                        if (list.isEmpty()) {
                            univAllLastCalledPostId = -1
                            _univAllList.update { originalList -> originalList.plus(PostListItem.NoMorePost) }
                        }
                        _univAllList.update { originalList ->
                            originalList
                                .plus(list.sortedByDescending { it.postId })
                                .filter { it !is PostListItem.Loading }
                        }
                    }
                    .onFailure {
                        _univAllList.update { list -> list.filter { it !is PostListItem.Loading } }
                        univAllLastCalledPostId = t
                    }
            }
            _loading.postValue(Event(false))
        }
    }

    fun loadNextAllUnivPostsIfNeeded(
        canScrollVertical: Boolean,
        lastVisibleItemPosition: Int,
    ) {
        if (loadNextAllUnivJob?.isActive == true) return
        loadNextAllUnivJob = viewModelScope.launch {
            if ((!canScrollVertical && lastVisibleItemPosition == univAllList.value.filter { it !is PostListItem.Loading }.lastIndex && univAllList.value
                    .isNotEmpty())
                || (lastVisibleItemPosition == univAllList.value.lastIndex - 10)
            ) {
                val lastPostId = (_univAllList.value.lastOrNull { it is PostResult } as? PostResult)?.postId ?: Int.MAX_VALUE
                if (univAllLastCalledPostId > lastPostId) {
                    _univAllList.update { it.plus(PostListItem.Loading) }
                    val t = univAllLastCalledPostId
                    univAllLastCalledPostId = lastPostId
                    repository.getAllUnivPostList(lastPostId)
                        .onSuccess { list ->
                            if (list.isEmpty()) {
                                univAllLastCalledPostId = -1
                                _univAllList.update { originalList -> originalList.plus(PostListItem.NoMorePost) }
                            }
                            _univAllList.update { originList ->
                                originList.plus(list
                                    .sortedByDescending { it.postId })
                                    .filter { it !is PostListItem.Loading }
                            }
                        }
                        .onFailure {
                            _univAllList.update { list -> list.filter { it !is PostListItem.Loading } }
                            univAllLastCalledPostId = t
                        }
                }
            }
        }
    }

    fun loadNewAllTotalPosts() {
        viewModelScope.launch {
            totalAllLastCalledPostId = Int.MAX_VALUE
            _loading.postValue(Event(true))
            repository.getAllTotalPostList(Int.MAX_VALUE)
                .onSuccess { list -> _totalAllList.value = list.sortedByDescending { it.postId } }
                .onFailure { _totalAllList.value = emptyList() }
            _loading.postValue(Event(false))
        }
    }

    fun loadNextAllTotalPosts() {
        viewModelScope.launch {
            _loading.postValue(Event(true))
            val lastPostId = (totalAllList.value.lastOrNull { it is PostResult } as PostResult?)?.postId ?: Int.MAX_VALUE
            if (totalAllLastCalledPostId > lastPostId) {
                val t = totalAllLastCalledPostId
                totalAllLastCalledPostId = lastPostId
                repository.getAllTotalPostList(lastPostId)
                    .onSuccess { list ->
                        if (list.isEmpty()) {
                            totalAllLastCalledPostId = -1
                            _totalAllList.update { originalList -> originalList.plus(PostListItem.NoMorePost) }
                        }
                        _totalAllList.update { current ->
                            current
                                .plus(list.sortedByDescending { it.postId })
                                .filter { it !is PostListItem.Loading }
                        }
                    }
                    .onFailure {
                        _totalAllList.update { list -> list.filter { it !is PostListItem.Loading } }
                        totalAllLastCalledPostId = t
                    }
            }
            _loading.postValue(Event(false))
        }
    }

    fun loadNextAllTotalPostsIfNeeded(
        canScrollVertical: Boolean,
        lastVisibleItemPosition: Int,
    ) {
        viewModelScope.launch {
            if ((!canScrollVertical
                        && lastVisibleItemPosition == totalAllList.value.filter { it !is PostListItem.Loading }.lastIndex
                        && totalAllList.value.isNotEmpty())
                || (lastVisibleItemPosition == totalAllList.value.lastIndex - 10)
            ) {
                val lastPostId = (totalAllList.value.lastOrNull { it is PostResult } as PostResult?)?.postId ?: Int.MAX_VALUE
                if (totalAllLastCalledPostId > lastPostId) {
                    val t = totalAllLastCalledPostId
                    totalAllLastCalledPostId = lastPostId
                    repository.getAllTotalPostList(lastPostId)
                        .onSuccess { list ->
                            if (list.isEmpty()) {
                                totalAllLastCalledPostId = -1
                                _totalAllList.update { originalList -> originalList.plus(PostListItem.NoMorePost) }
                            }
                            _totalAllList.update { current ->
                                current
                                    .plus(list.sortedByDescending { it.postId })
                                    .filter { it !is PostListItem.Loading }
                            }
                        }
                        .onFailure {
                            _totalAllList.update { list -> list.filter { it !is PostListItem.Loading } }
                            totalAllLastCalledPostId = t
                        }
                }
            }
        }
    }

    fun getTotalList(category: Int, isRefreshing: Boolean) {
        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            when (category) {
                1 -> {
                    totalFreeLastCalledPostId = Int.MAX_VALUE
                }

                2 -> {
                    totalQuestionLastCalledPostId = Int.MAX_VALUE
                }

                4 -> {
                    totalMingleLastCalledPostId = Int.MAX_VALUE
                }
            }
            repository.getTotalPost(category, Int.MAX_VALUE)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))

                        if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                            when (category) {
                                1 -> {
                                    _univTotalList1.postValue(response.body()!!.result.postListDTO)
                                    val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                    _lastPostId1.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                                }

                                2 -> {
                                    _univTotalList2.postValue(response.body()!!.result.postListDTO)
                                    val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                    _lastPostId2.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                                }

                                3 -> {
                                    _univTotalList3.postValue(response.body()!!.result.postListDTO)
                                    val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                    _lastPostId3.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                                }

                                4 -> {
                                    _univTotalList4.postValue(response.body()!!.result.postListDTO)
                                    val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                    _lastPostId4.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                                }
                            }
                        } else if (response.body()!!.code == 3031) {
                            when (category) {
                                1 -> _univTotalList1.postValue(emptyList())
                                2 -> _univTotalList2.postValue(emptyList())
                                3 -> _univTotalList3.postValue(emptyList())
                                4 -> _univTotalList4.postValue(emptyList())
                            }
                        }
                    } else {
                        Log.d("tag_fail", "getTotalList Error: ${response.code()}")
                    }
                }
        }
    }

    fun getUnivNextPosts(category: Int, lastPostId: Int) {
        viewModelScope.launch {
            val t =
                when (category) {
                    1 -> {
                        if (univFreeLastCalledPostId <= lastPostId) return@launch
                        univFreeLastCalledPostId.also { univFreeLastCalledPostId = lastPostId }
                    }

                    2 -> {
                        if (univQuestionLastCalledPostId <= lastPostId) return@launch
                        univQuestionLastCalledPostId.also { univQuestionLastCalledPostId = lastPostId }
                    }

                    5 -> {
                        if (univCouncilLastCalledPostId <= lastPostId) return@launch
                        univCouncilLastCalledPostId.also { univCouncilLastCalledPostId = lastPostId }
                    }

                    else -> {
                        return@launch
                    }
                }
            _loading.postValue(Event(true))
            when (category) {
                1 -> {
                    _univTotalList1.postValue(listOf(PostListItem.Loading))
                }

                2 -> {
                    _univTotalList2.postValue(listOf(PostListItem.Loading))
                }

                5 -> {
                    _univTotalList4.postValue(listOf(PostListItem.Loading))
                }
            }
            repository.getUnivPost(category, lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getNextPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        when (category) {
                            1 -> {
                                _univTotalList1.postValue(response.body()!!.result.postListDTO)
                                val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                _lastPostId1.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                            }

                            2 -> {
                                _univTotalList2.postValue(response.body()!!.result.postListDTO)
                                val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                _lastPostId2.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                            }

                            5 -> {
                                _univTotalList4.postValue(response.body()!!.result.postListDTO)
                                val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                _lastPostId4.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                            }
                        }
                    } else if (response.body()!!.code == 3031) {
                        when (category) {
                            1 -> {
                                _univTotalList1.postValue(listOf(PostListItem.NoMorePost))
                                _lastPostId1.postValue(-1)
                                univFreeLastCalledPostId = -1
                            }

                            2 -> {
                                _univTotalList2.postValue(listOf(PostListItem.NoMorePost))
                                _lastPostId2.postValue(-1)
                                univQuestionLastCalledPostId = -1
                            }

                            5 -> {
                                _univTotalList4.postValue(listOf(PostListItem.NoMorePost))
                                _lastPostId4.postValue(-1)
                                univCouncilLastCalledPostId = -1
                            }
                        }
                    }
                } else {
                    when (category) {
                        1 -> univFreeLastCalledPostId = t
                        2 -> univQuestionLastCalledPostId = t
                        5 -> univCouncilLastCalledPostId = t
                    }

                    when (category) {
                        1 -> {
                            _univTotalList1.postValue(emptyList())
                        }

                        2 -> {
                            _univTotalList2.postValue(emptyList())
                        }

                        5 -> {
                            _univTotalList4.postValue(emptyList())
                        }
                    }
                    Log.d("tag_fail", "getUnivNextPosts Error: ${response.code()}")
                }
            }.onFailure {
                when (category) {
                    1 -> univFreeLastCalledPostId = t
                    2 -> univQuestionLastCalledPostId = t
                    5 -> univCouncilLastCalledPostId = t
                }


                when (category) {
                    1 -> {
                        _univTotalList1.postValue(emptyList())
                    }

                    2 -> {
                        _univTotalList2.postValue(emptyList())
                    }

                    5 -> {
                        _univTotalList4.postValue(emptyList())
                    }
                }
            }
        }
    }

    fun getTotalNextPosts(category: Int, lastPostId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val t =
                when (category) {
                    1 -> {
                        if (totalFreeLastCalledPostId <= lastPostId) return@launch
                        totalFreeLastCalledPostId.also { totalFreeLastCalledPostId = lastPostId }
                    }

                    2 -> {
                        if (totalQuestionLastCalledPostId <= lastPostId) return@launch
                        totalQuestionLastCalledPostId.also { totalQuestionLastCalledPostId = lastPostId }
                    }

                    4 -> {
                        if (totalMingleLastCalledPostId <= lastPostId) return@launch
                        totalMingleLastCalledPostId.also { totalMingleLastCalledPostId = lastPostId }
                    }

                    else -> {
                        return@launch
                    }
                }
            when (category) {
                1 -> {
                    _univTotalList1.postValue(listOf(PostListItem.Loading))
                }

                2 -> {
                    _univTotalList2.postValue(listOf(PostListItem.Loading))
                }

                4 -> {
                    _univTotalList4.postValue(listOf(PostListItem.Loading))
                }
            }
            _loading.postValue(Event(true))
            repository.getTotalPost(category, lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getNextPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        when (category) {
                            1 -> {
                                _univTotalList1.postValue(response.body()!!.result.postListDTO)
                                val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                _lastPostId1.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                            }

                            2 -> {
                                _univTotalList2.postValue(response.body()!!.result.postListDTO)
                                val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                _lastPostId2.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                            }

                            4 -> {
                                _univTotalList4.postValue(response.body()!!.result.postListDTO)
                                val lastIdx = response.body()!!.result.postListDTO.lastIndex
                                _lastPostId4.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                            }
                        }
                    } else if (response.body()!!.code == 3031) {
                        when (category) {
                            1 -> {
                                _univTotalList1.postValue(listOf(PostListItem.NoMorePost))
                                _lastPostId1.postValue(-1)
                                totalFreeLastCalledPostId = -1
                            }

                            2 -> {
                                _univTotalList2.postValue(listOf(PostListItem.NoMorePost))
                                _lastPostId2.postValue(-1)
                                totalQuestionLastCalledPostId = -1
                            }

                            4 -> {
                                _univTotalList4.postValue(listOf(PostListItem.NoMorePost))
                                _lastPostId4.postValue(-1)
                                totalMingleLastCalledPostId = -1
                            }
                        }
                    }
                } else {
                    when (category) {
                        1 -> {
                            _univTotalList1.postValue(emptyList())
                            totalFreeLastCalledPostId = t
                        }

                        2 -> {
                            _univTotalList2.postValue(emptyList())
                            totalQuestionLastCalledPostId = t
                        }

                        4 -> {
                            _univTotalList4.postValue(emptyList())
                            totalMingleLastCalledPostId = t
                        }
                    }
                    Log.d("tag_fail", "getTotalNextPosts Error: ${response.code()}")
                }
            }.onFailure {
                when (category) {
                    1 -> {
                        _univTotalList1.postValue(emptyList())
                        totalFreeLastCalledPostId = t
                    }

                    2 -> {
                        _univTotalList2.postValue(emptyList())
                        totalQuestionLastCalledPostId = t
                    }

                    4 -> {
                        _univTotalList4.postValue(emptyList())
                        totalMingleLastCalledPostId = t
                    }
                }
            }
        }
    }

    fun loadNextUnivIfNeeded(
        canScrollVertical: Boolean,
        lastVisiblePostPosition: Int,
        lastPostId: Int,
        totalCount: Int,
        category: Int,
    ) {
        viewModelScope.launch {
            if ((!canScrollVertical && lastVisiblePostPosition == totalCount && lastPostId != -1)
                || (lastVisiblePostPosition == totalCount - 10)
            ) {
                getUnivNextPosts(category, lastPostId)
            }
        }
    }

    fun loadNextTotalIfNeeded(
        canScrollVertical: Boolean,
        lastVisiblePostPos: Int,
        lastPostId: Int,
        totalCount: Int,
        category: Int,
    ) {
        viewModelScope.launch {
            if ((!canScrollVertical && lastVisiblePostPos == totalCount && lastPostId != -1)
                || (lastVisiblePostPos == totalCount - 10)
            ) {
                getTotalNextPosts(category, lastPostId)
            }
        }
    }
}