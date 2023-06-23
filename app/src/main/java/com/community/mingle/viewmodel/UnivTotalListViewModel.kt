package com.community.mingle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.service.models.HomeResult
import com.community.mingle.service.models.PostResult
import com.community.mingle.service.repository.UnivTotalRepository
import com.community.mingle.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnivTotalListViewModel
@Inject
constructor(
    private val repository: UnivTotalRepository
) : ViewModel() {

    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading

    /* 잔디밭 / 광장 게시물 리스트 불러오기 */
    private val _univTotalList1 = MutableLiveData<List<PostResult>>()
    val univTotalList1: LiveData<List<PostResult>> get() = _univTotalList1

    private val _univTotalList2 = MutableLiveData<List<PostResult>>()
    val univTotalList2: LiveData<List<PostResult>> get() = _univTotalList2

    private val _univTotalList3 = MutableLiveData<List<PostResult>>()
    val univTotalList3: LiveData<List<PostResult>> get() = _univTotalList3

    private val _univTotalList4 = MutableLiveData<List<PostResult>>()
    val univTotalList4: LiveData<List<PostResult>> get() = _univTotalList4

    private val _univTotalList5 = MutableLiveData<List<PostResult>>()
    val univTotalList5: LiveData<List<PostResult>> get() = _univTotalList5

    private val _lastPostId1 = MutableLiveData<Int>()
    val lastPostId1: LiveData<Int> get() = _lastPostId1

    private val _lastPostId2 = MutableLiveData<Int>()
    val lastPostId2: LiveData<Int> get() = _lastPostId2

    private val _lastPostId3 = MutableLiveData<Int>()
    val lastPostId3: LiveData<Int> get() = _lastPostId3

    private val _lastPostId4 = MutableLiveData<Int>()
    val lastPostId4: LiveData<Int> get() = _lastPostId4

    private val _lastPostId5 = MutableLiveData<Int>()
    val lastPostId5: LiveData<Int> get() = _lastPostId5

    private val _newUnivTotalList1 = MutableLiveData<List<PostResult>>()
    val newUnivTotalList1: LiveData<List<PostResult>> get() = _newUnivTotalList1

    private val _newUnivTotalList2 = MutableLiveData<List<PostResult>>()
    val newUnivTotalList2: LiveData<List<PostResult>> get() = _newUnivTotalList2

    private val _newUnivTotalList3 = MutableLiveData<List<PostResult>>()
    val newUnivTotalList3: LiveData<List<PostResult>> get() = _newUnivTotalList3

    private val _newUnivTotalList4 = MutableLiveData<List<PostResult>>()
    val newUnivTotalList4: LiveData<List<PostResult>> get() = _newUnivTotalList4

    private val _newUnivTotalList5 = MutableLiveData<List<PostResult>>()
    val newUnivTotalList5: LiveData<List<PostResult>> get() = _newUnivTotalList5

    private val _clearUnivTotalList = MutableLiveData<Event<Boolean>>()
    val clearUnivTotalList: LiveData<Event<Boolean>> = _clearUnivTotalList

//    fun updateList(postList: Array<PostResult>) {
//        _newUnivTotalList.postValue(postList.toList())
//    }

    // isRefreshing 추후에 추가
    fun getUnivList(category: Int, isRefreshing: Boolean) {

        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getUnivPost(category, 100000000)
                .let { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))
                        if ( response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
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
                        }
                        else if (response.body()!!.code == 3031) {
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
                }
        }
    }

    fun getTotalList(category: Int, isRefreshing: Boolean) {

        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getTotalPost(category, 1000000)
                .let { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))

                        if ( response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
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
                        }
                        else if (response.body()!!.code == 3031) {
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

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getUnivPost(category, lastPostId).let { response ->
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
                    }
                    else if (response.body()!!.code == 3031) {
                        when (category) {
                            1 -> _lastPostId1.postValue(-1)
                            2 -> _lastPostId2.postValue(-1)
                            3 -> _lastPostId3.postValue(-1)
                            5 -> _lastPostId4.postValue(-1)
                        }
                    }
                } else {
                    Log.d("tag_fail", "getUnivNextPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getTotalNextPosts(category: Int, lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getTotalPost(category, lastPostId).let { response ->
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
                    }
                    else if (response.body()!!.code == 3031) {
                        when (category) {
                            1 -> _lastPostId1.postValue(-1)
                            2 -> _lastPostId2.postValue(-1)
                            3 -> _lastPostId3.postValue(-1)
                            4 -> _lastPostId4.postValue(-1)
                        }
                    }
                } else {
                    Log.d("tag_fail", "getTotalNextPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getUnivBestPosts(isRefreshing: Boolean) {
        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getUnivBestPost(100000000)
                .let { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))
                        if ( response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                            _univTotalList5.postValue(response.body()!!.result.postListDTO)
                            val lastIdx = response.body()!!.result.postListDTO.lastIndex
                            _lastPostId5.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                            Log.d("tag_success", response.body().toString())
                        }
                        else if (response.body()!!.code == 3031) {
                            _univTotalList5.postValue(emptyList())
                        }
                    } else {
                        Log.d("tag_fail", "getUnivList Error: ${response.code()}")
                    }
                }
        }

    }

    fun getUnivNextBestPosts( lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getUnivBestPost( lastPostId).let { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getNextPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _univTotalList5.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId5.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                } else {
                    Log.d("tag_fail", "getUnivNextPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getTotalBestPosts(isRefreshing: Boolean) {
        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getTotalBestPost(100000000)
                .let { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))
                        if ( response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                            _univTotalList5.postValue(response.body()!!.result.postListDTO)
                            val lastIdx = response.body()!!.result.postListDTO.lastIndex
                            _lastPostId5.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                            Log.d("tag_success", response.body().toString())
                        }
                        else if (response.body()!!.code == 3031) {
                            _univTotalList5.postValue(emptyList())
                        }
                    } else {
                        Log.d("tag_fail", "getTotalList Error: ${response.code()}")
                    }
                }
        }

    }

    fun getTotalNextBestPosts( lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getTotalBestPost( lastPostId).let { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getNextPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _univTotalList5.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId5.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                } else {
                    Log.d("tag_fail", "getTotalNextPosts Error: ${response.code()}")
                }
            }
        }
    }


}