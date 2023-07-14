package com.community.mingle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.MingleApplication
import com.community.mingle.service.models.FcmToken
import com.community.mingle.service.models.OldUser
import com.community.mingle.service.repository.MemberRepository
import com.community.mingle.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val repository: MemberRepository
) : ViewModel() {

    val email = MutableLiveData("")
    val pw = MutableLiveData("")
    val fcmtoken = MutableLiveData("")

    // 로그인 성공 여부
    private val _loginSuccess = MutableLiveData<Event<String>>()
    val loginSuccess: LiveData<Event<String>> = _loginSuccess

    private val _fcmRefreshSuccess = MutableLiveData<Event<Boolean>>()
    val fcmRefreshSuccess: LiveData<Event<Boolean>> = _fcmRefreshSuccess

    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading

    // 경고 메시지
    private val _alertMsg = MutableLiveData<Event<String>>()
    val alertMsg: LiveData<Event<String>> = _alertMsg

    // 입력받은 정보를 확인해 그에 따른 에러 메시지를 이벤트 처리
    private fun checkInfo(): Boolean {
        var checkValue = true
        // 아이디 값 확인
        if (email.value?.isBlank() == true) {
            _alertMsg.postValue(Event("이메일을 입력해주세요"))
            checkValue = false
        }

        // 비밀번호 값 확인
        if (pw.value?.isBlank() == true) {
            _alertMsg.postValue(Event("비밀번호를 입력해주세요"))
            checkValue = false
        }

        return checkValue
    }

    fun login() {
        if (!checkInfo()) return

        val userEmail = email.value?.trim() ?: return
        val userPw = pw.value ?: return
//        var userToken = fcmtoken.value ?: return
//        if (MingleApplication.pref.fcmToken != null)
//            userToken = MingleApplication.pref.fcmToken.toString()

        val loginInfo = OldUser(userEmail, userPw, MingleApplication.pref.fcmToken.toString())

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.login(loginInfo).onSuccess { response ->
                if (response.isSuccessful) {
                    when (response.body()!!.code) {
                        1000 -> {
                            _loginSuccess.postValue(Event(userEmail))
                            _loading.postValue(Event(false))
                            MingleApplication.pref.accessToken =
                                response.body()!!.result!!.accessToken
                            MingleApplication.pref.refreshToken =
                                response.body()!!.result!!.refreshToken
                            MingleApplication.pref.email = response.body()!!.result!!.email
                            MingleApplication.pref.nickname = response.body()!!.result!!.nickName
                            MingleApplication.pref.univName = response.body()!!.result!!.univName
                            Log.d("tag_success", "login: ${response.body()}")
                        }
                        3011 -> {
                            _alertMsg.postValue(Event("일치하는 이메일이나 비밀번호를 찾지 못했습니다."))
                            _loading.postValue(Event(false))
                            Log.d("tag_fail", "login Error: ${response.code()}")
                        }
                        3017 -> {
                            _alertMsg.postValue(Event("일치하는 이메일이나 비밀번호를 찾지 못했습니다."))
                            _loading.postValue(Event(false))
                            Log.d("tag_fail", "login Error: ${response.code()}")
                        }
                    }
                } else {
                    Log.d("tag_fail", "login Error: ${response.code()}")
                    _loading.postValue(Event(false))
                }
            }
                .onFailure {
                    _alertMsg.postValue(Event("네트워크 상태가 좋지 않습니다."))
                    _loading.postValue(Event(false))
                }
        }
    }

    fun fcmRefresh() {
        viewModelScope.launch(Dispatchers.IO) {
            MingleApplication.pref.fcmToken?.let { FcmToken(it) }?.let {
                repository.fcmRefresh(it).onSuccess { response ->
                    if (response.isSuccessful) {
                        when (response.body()!!.code) {
                            1000 -> {
                                _fcmRefreshSuccess.postValue(Event(true))
                                Log.d("tag_success", "fcm-refresh: ${response.body()}")
                            }
                            else -> {}
                        }
                    } else {
                        Log.d("tag_fail", "fcm refresh Error: ${response.code()}")
                    }
                }
            }
        }

    }
}