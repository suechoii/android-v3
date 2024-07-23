package com.community.mingle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.model.post.HomeHotPost
import com.community.mingle.service.models.Banner
import com.community.mingle.service.models.HomeResult
import com.community.mingle.service.models.NotiData
import com.community.mingle.service.models.Notifications
import com.community.mingle.service.repository.HomeRepository
import com.community.mingle.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val repository: HomeRepository,
) : ViewModel() {
    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading
    private val _banner = MutableLiveData<List<Banner>>()
    val banner: LiveData<List<Banner>> get() = _banner
    private val _homeUnivRecentList = MutableLiveData<List<HomeResult>>()
    val homeUnivRecentList: LiveData<List<HomeResult>> get() = _homeUnivRecentList
    private val _homeTotalRecentList = MutableLiveData<List<HomeResult>>()
    val homeTotalRecentList: LiveData<List<HomeResult>> get() = _homeTotalRecentList
    private val _homeHotPostList = MutableStateFlow<List<HomeResult>>(emptyList())
    val homeHotPostList = _homeHotPostList.asStateFlow()
    private val _getNotificationSuccess = MutableLiveData<Event<Boolean>>()
    val getNotificationSuccess: LiveData<Event<Boolean>> = _getNotificationSuccess
    private val _readNotificationSuccess = MutableLiveData<Event<Boolean>>()
    val readNotificationSuccess: LiveData<Event<Boolean>> = _readNotificationSuccess
    private val _notiList = MutableLiveData<List<NotiData>>()
    val notiList: LiveData<List<NotiData>> get() = _notiList
    private val _refreshExpire = MutableLiveData<Boolean>()
    val refreshExpire: LiveData<Boolean> = _refreshExpire

    init {
        getHomeList()
        getUnivRecent()
        getTotalRecent()
        loadBestPostList()
    }

    fun getHomeList() {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getBanner().onSuccess { response ->
                if (response.isSuccessful && response.body()?.code == 1000) {
                    Log.d("tag_success", "getBannerList: ${response.body()}")
                    _banner.postValue(response.body()!!.result)
                    //getUnivRecent()
                }
            }.onFailure {
                // TODO: 배너 불러오기 실패 오류
            }
            _loading.postValue(Event(false))
        }
    }

    fun getUnivRecent() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getUnivRecentPost().onSuccess { response ->
                if (response.isSuccessful) {
                    if (response.body()?.code == 1000) {
                        if (response.body()!!.result.isNotEmpty()) {
                            Log.d("tag_success", "getUnivRecentList: ${response.body()}")
                            _homeUnivRecentList.postValue(response.body()!!.result)
                            //getTotalRecent()
                        }
                    }
                } else {
                    Log.d("tag_", "getUnivRecentList Error: ${response.code()}")
                }
            }.onFailure {
                // TODO: 오류 메시지 띄우기
            }
            _loading.postValue(Event(false))
        }
    }

    fun getTotalRecent() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTotalRecentPost().onSuccess { response ->
                if (response.isSuccessful) {
                    if (response.body()?.code == 1000) {
                        Log.d("tag_success", "getTotalRecentList: ${response.body()}")
                        _homeTotalRecentList.postValue(response.body()!!.result)
                    }
                } else {
                    //                    if (response.code() == 403) {
                    //                        refresh()
                    //                    }
                    Log.d("tag_", "getTotalRecentList Error: ${response.code()}")
                }
            }.onFailure {
                // TODO: 오류 메시지 띄우기
            }
        }
    }

    fun getNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getNotification().onSuccess { response ->
                if (response.isSuccessful && response.body()!!.code == 1000) {
                    _getNotificationSuccess.postValue(Event(true))
                    _notiList.postValue(response.body()!!.result)
                    Log.d("tag_success", "get Notification: ${response.body()}")
                } else {
                    //                    if (response.code() == 403) {
                    //                        refresh()
                    //                    }
                    Log.d("tag_fail", "getNoti Error: ${response.code()}")
                }
            }.onFailure {
                // error on load notification show nothing
            }
        }
    }


    fun loadBestPostList() {
        viewModelScope.launch {
            repository.getUniteBestList()
                .catch {
                    _homeHotPostList.value = emptyList()
                }
                .collect { list ->
                    _homeHotPostList.value = list
                }
        }
    }

    fun readNotification(type: String, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.readNotification(Notifications(type, id)).onSuccess { response ->
                if (response.isSuccessful) {
                    _readNotificationSuccess.postValue(Event(true))
                    Log.d("tag_success", "Read Notification: ${response.body()}")
                } else {
                    //                    if (response.code() == 403) {
                    //                        refresh()
                    //                    }
                    Log.d("tag_fail", "readNoti Error: ${response.code()}")
                }
            }.onFailure {
                // error on load notification show nothing
            }
        }
    }
}
