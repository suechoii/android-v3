package com.community.mingle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.service.models.PostResult
import com.community.mingle.service.repository.UnivTotalRepository
import com.community.mingle.utils.Constants.get
import com.community.mingle.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val repository: UnivTotalRepository,
) : ViewModel() {

    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading
    private val _searchUnivList = MutableLiveData<List<PostResult>>()
    val searchUnivList: LiveData<List<PostResult>> get() = _searchUnivList
    private val _searchTotalList = MutableLiveData<List<PostResult>>()
    val searchTotalList: LiveData<List<PostResult>> get() = _searchTotalList
    private val _newUnivList = MutableLiveData<List<PostResult>>()
    val newUnivList: LiveData<List<PostResult>> get() = _newUnivList
    private val _newTotalList = MutableLiveData<List<PostResult>>()
    val newTotalList: LiveData<List<PostResult>> get() = _newTotalList

    fun isNewUnivList() {
        _newUnivList.postValue(emptyList())
    }

    fun isNewTotalList() {
        _newTotalList.postValue(emptyList())
    }

    fun getUnivSearchList(keyword: String, isRefreshing: Boolean) {
        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.searchUnivPost(keyword)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))
                        if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                            _searchUnivList.postValue(response.body()!!.result.postListDTO)
                        } else if (response.body()!!.code == 3035) {
                            _searchUnivList.postValue(emptyList())
                        }
                    } else {
                        Log.d("tag_fail", "getUnivList Error: ${response.code()}")
                    }
                }
        }
    }

    fun getTotalSearchList(keyword: String, isRefreshing: Boolean) {
        if (isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.searchTotalPost(keyword)
                .onSuccess { response ->
                    if (response.isSuccessful) {
                        if (!isRefreshing)
                            _loading.postValue(Event(false))
                        if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                            _searchTotalList.postValue(
                                searchTotalList.value?.plus(response.body()!!.result.postListDTO) ?: response.body()!!
                                    .result.postListDTO
                            )
                        } else if (response.body()!!.code == 3035) {
                            _searchTotalList.postValue(emptyList())
                        }
                    } else {
                        Log.d("tag_fail", "getUnivList Error: ${response.code()}")
                    }
                }
        }
    }
}