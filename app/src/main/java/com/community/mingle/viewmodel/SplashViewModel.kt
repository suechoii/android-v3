package com.community.mingle.viewmodel

import android.content.ContentValues
import com.community.mingle.service.repository.AuthRepository

import android.util.Log
import androidx.lifecycle.*
import com.community.mingle.MingleApplication
import com.community.mingle.service.models.Email
import com.community.mingle.service.repository.HomeRepository
import com.community.mingle.service.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject
constructor(
    private val homeRepository: HomeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // 토큰의 유효성 여부
    private val _tokenVerified = MutableLiveData<Boolean>()
    val tokenVerified: LiveData<Boolean> = _tokenVerified

    // 1초 뒤 값이 들어오는 LiveData
    private val _time = MutableLiveData<Boolean>()
    val time: LiveData<Boolean> = _time

    private val _refreshExpire = MutableLiveData<Boolean>()
    val refreshExpire : LiveData<Boolean> = _refreshExpire

    // 자동 로그인
    private val _autoLogin = combine(
        tokenVerified.asFlow(),
        time.asFlow(),
    ) { token, time ->
        token && time
    }.onStart { emit(false) }.asLiveData()
    val autoLogin: LiveData<Boolean> get() = _autoLogin

    // 로그인 화면으로 이동 필요
    private val _moveLogin = combine(
        tokenVerified.asFlow(),
        time.asFlow(),
    ) { token, time ->
        !token && time
    }.onStart { emit(false) }.asLiveData()
    val moveLogin: LiveData<Boolean> get() = _moveLogin

    init {
        checkLogin()
    }

    private fun checkLogin() {

        // 1초 후 time 값 true
        viewModelScope.launch() {
            delay(1000)
            _time.postValue(true)
        }

        // 토큰 검사
        viewModelScope.launch(Dispatchers.IO) {
            val refreshToken = MingleApplication.pref.refreshToken // refresh 토큰

            if (!refreshToken.isNullOrEmpty()) { // 저장된 토큰이 있는 경우
                homeRepository.getBanner().let { response ->
                    if (response.isSuccessful && response.body()!!.code == 1000) { // 토큰이 유효한 경우
                        _tokenVerified.postValue(true)
                    } else { // 토큰이 유효하지 않은 경우
//                        if (response.code() == 403) {
//                            //refresh()
//                        }
//                        else {
//                            _tokenVerified.postValue(false)
//                        }
                    }
                }
            } else { // 저장된 토큰이 없는 경우
                _tokenVerified.postValue(false)
            }
        }
    }

//    fun refresh () {
//        runBlocking {
//            try {
//                val refreshToken = MingleApplication.pref.refreshToken.toString()
//                val email = MingleApplication.pref.email.toString()
//                val response = authRepository.refresh(Email(email),refreshToken)
//                if (response.isSuccessful && response.body()!!.code == 1000) {
//                    MingleApplication.pref.refreshToken = response.body()!!.result.refreshToken
//                    MingleApplication.pref.accessToken = response.body()!!.result.accessToken
//                    _tokenVerified.postValue(true)
//                    Log.d("token verified","true")
//                    //checkLogin()
//                } else {
//                    _refreshExpire.postValue(true)
//                }
//            } catch (e: Exception) {
//                Log.d(ContentValues.TAG, "e: ${e.message}")
//                _refreshExpire.postValue(true)
//            }
//        }
//    }
}