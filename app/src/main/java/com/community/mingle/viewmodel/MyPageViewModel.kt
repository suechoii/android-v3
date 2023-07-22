package com.community.mingle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.MingleApplication
import com.community.mingle.service.models.*
import com.community.mingle.service.repository.MyPageRepository
import com.community.mingle.utils.Constants.get
import com.community.mingle.utils.Event
import com.community.mingle.utils.SignupChangepwUtils
import com.community.mingle.utils.SignupChangepwUtils.EMAIL_ERROR
import com.community.mingle.utils.SignupChangepwUtils.NICKNAME_DUP
import com.community.mingle.utils.SignupChangepwUtils.USER_ERROR
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel
@Inject
constructor(
    private val repository: MyPageRepository
) : ViewModel() {

    val univs = mutableMapOf<String, Int>()

    // nickname
    val nickname: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // email
    val email: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // verification code
    val code: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // password
    val password: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // confirm password
    val confirmPw: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // 탈퇴시 email
    val quitEmail: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // 탈퇴시 password
    val quitPassword: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading

    private val _isEmailVerified = MutableLiveData<String>()
    val isEmailVerified: LiveData<String> get() = _isEmailVerified

    private val _isCodeVerified = MutableLiveData<String>()
    val isCodeVerified: LiveData<String> get() = _isCodeVerified

    private val _isPwVerified = MutableLiveData<String>()
    val isPwVerified: LiveData<String> get() = _isPwVerified

    private val _isPwConfirmVerified = MutableLiveData<String>()
    val isPwConfirmVerified: LiveData<String> get() = _isPwConfirmVerified

    private val _isNicknameVerified = MutableLiveData<String>()
    val isNicknameVerified: LiveData<String> get() = _isNicknameVerified

    private val _postList = MutableLiveData<List<PostResult>>()
    val postList: LiveData<List<PostResult>> get() = _postList

    private val _lastPostId = MutableLiveData<Int>()
    val lastPostId: LiveData<Int> get() = _lastPostId

    private val _newUnivTotalList = MutableLiveData<List<PostResult>>()
    val newUnivTotalList: LiveData<List<PostResult>> get() = _newUnivTotalList

    private val _clearUnivTotalList = MutableLiveData<Event<Boolean>>()
    val clearUnivTotalList: LiveData<Event<Boolean>> = _clearUnivTotalList

    private val _getUnivListSuccess = MutableLiveData<Event<Boolean>>()
    val getUnivListSuccess: LiveData<Event<Boolean>> = _getUnivListSuccess

    private val _getDomainSuccess = MutableLiveData<Event<Boolean>>()
    val getDomainSuccess: LiveData<Event<Boolean>> = _getDomainSuccess

    private val _sendCodeSuccess = MutableLiveData<Event<Boolean>>()
    val sendCodeSuccess: LiveData<Event<Boolean>> = _sendCodeSuccess

    private val _resetSuccess = MutableLiveData<Event<Boolean>>()
    val resetSuccess: LiveData<Event<Boolean>> = _resetSuccess

    private val _isTermSuccess = MutableLiveData<Event<Boolean>>()
    val isTermSuccess: LiveData<Event<Boolean>> = _isTermSuccess

    private val _isDeleteAccount = MutableLiveData<String>()
    val isDeleteAccount: LiveData<String> get() = _isDeleteAccount

    private var univId : Int = 0
    private var userEmail = ""
    var domain = ""
    private var userPw = ""
    var terms = ""

    fun validateNickname() {
        modifyNickname(nickname.value!!)
    }

    private fun modifyNickname(nickname: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.modifyNickname(Nickname(nickname)).onSuccess { response ->
                if (response.isSuccessful  ) {
                    when (response.body()!!.code) {
                        1000 ->  {
                            _isNicknameVerified.postValue("")
                            MingleApplication.pref.nickname = nickname
                        }
                        2017 -> {
                            _isNicknameVerified.postValue(nickname+NICKNAME_DUP)
                        }
                        else -> {
                            _isNicknameVerified.postValue(" ")
                        }
                    }
                }
            }
        }
    }

    fun getMyUnivPostList(postId: Int, isRefreshing: Boolean) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyUnivPost(postId).onSuccess { response ->
                if (response.isSuccessful) {

                    _loading.postValue(Event(false))

                    Log.d("tag_success", "getMyUnivPostList: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId.toInt())
                    }
                } else {
                    Log.d("tag_fail", "getMyUnivPostList Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyTotalPostList(postId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyTotalPost(postId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))

                    Log.d("tag_success", "getMyTotalPostList: ${response.body()}")

                    if (response.body()!!.code == 1000 && !response.body()!!.result.postListDTO.isNullOrEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                } else {
                    Log.d("tag_fail", "getMyTotalPostList Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyUnivNextPosts(lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyUnivPost(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getMyUnivNextPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                        Log.d("추가",lastIdx.toString())
                    }
                    else if (response.body()!!.code == 3031) {
                        _lastPostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getUnivNextPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyTotalNextPosts(lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyTotalPost(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getMyTotalNextPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                        Log.d("추가",lastIdx.toString())
                    }
                    else if (response.body()!!.code == 3031) {
                        _lastPostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getMyTotalNextPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyUnivCommentPostList(postId: Int, isRefreshing: Boolean) {

        if (!isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyUnivComment(postId).onSuccess { response ->
                if (response.isSuccessful) {

                    if (!isRefreshing)
                        _loading.postValue(Event(false))

                    Log.d("tag_success", "getMyUnivCommentPostList: ${response.body()}")

                    if (response.body()!!.code == 1000 && !response.body()!!.result.postListDTO.isNullOrEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                } else {
                    Log.d("tag_fail", "getMyUnivCommentPostList Error: ${response.code()}")
                }
            }
        }
    }


    fun getMyTotalCommentPostList(postId: Int, isRefreshing: Boolean) {

        if (!isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyTotalComment(postId).onSuccess { response ->
                if (response.isSuccessful) {

                    if (!isRefreshing)
                        _loading.postValue(Event(false))

                    Log.d("tag_success", "getMyTotalCommentPostList: ${response.body()}")

                    if (response.body()!!.code == 1000 && !response.body()!!.result.postListDTO.isNullOrEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                } else {
                    Log.d("tag_fail", "getMyTotalCommentPostList Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyUnivNextCommentedPosts(lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyUnivComment(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getMyUnivNextCommentedPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                    else if (response.body()!!.code == 3031) {
                        _lastPostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getMyUnivNextCommentedPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyTotalNextCommentedPosts(lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyTotalComment(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getMyTotalNextCommentedPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                    else if (response.body()!!.code == 3031) {
                        _lastPostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getMyTotalNextCommentedPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyScrapUnivPostList(postId: Int, isRefreshing: Boolean) {

        if (!isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyUnivScrap(postId).onSuccess { response ->
                if (response.isSuccessful) {

                    if (!isRefreshing)
                        _loading.postValue(Event(false))

                    Log.d("tag_success", "getMyScrapUnivPostList: ${response.body()}")

                    if (response.body()!!.code == 1000 && !response.body()!!.result.postListDTO.isNullOrEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                } else {
                    Log.d("tag_fail", "getMyScrapUnivPostList Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyScrapTotalPostList(postId: Int, isRefreshing: Boolean) {

        if (!isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyTotalScrap(postId).onSuccess { response ->
                if (response.isSuccessful) {

                    if (!isRefreshing)
                        _loading.postValue(Event(false))

                    Log.d("tag_success", "getMyScrapTotalPostList: ${response.body()}")

                    if (response.body()!!.code == 1000 && !response.body()!!.result.postListDTO.isNullOrEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                } else {
                    Log.d("tag_fail", "getMyScrapTotalPostList Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyUnivNextScrapedPosts(lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyUnivScrap(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getMyUnivNextScrapedPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                    else if (response.body()!!.code == 3031) {
                        _lastPostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getMyUnivNextScrapedPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyTotalNextScrapedPosts(lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyTotalScrap(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getMyTotalNextScrapedPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                    else if (response.body()!!.code == 3031) {
                        _lastPostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getMyTotalNextScrapedPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyLikedUnivPostList(postId: Int, isRefreshing: Boolean) {

        if (!isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyLikedUniv(postId).onSuccess { response ->
                if (response.isSuccessful) {

                    if (!isRefreshing)
                        _loading.postValue(Event(false))

                    Log.d("tag_success", "getMyLikedUnivPostList: ${response.body()}")

                    if (response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                } else {
                    Log.d("tag_fail", "getMyLikedUnivPostList Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyLikedTotalPostList(postId: Int, isRefreshing: Boolean) {

        if (!isRefreshing)
            _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyLikedTotal(postId).onSuccess { response ->
                if (response.isSuccessful) {

                    if (!isRefreshing)
                        _loading.postValue(Event(false))

                    Log.d("tag_success", "getMyLikedTotalPostList: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                } else {
                    Log.d("tag_fail", "getMyLikedTotalPostList Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyUnivNextLikedPosts(lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyLikedUniv(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getMyUnivNextLikedPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                    else if (response.body()!!.code == 3031) {
                        _lastPostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getMyUnivNextLikedPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getMyTotalNextLikedPosts(lastPostId: Int) {

        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getMyLikedTotal(lastPostId).onSuccess { response ->
                if (response.isSuccessful) {
                    _loading.postValue(Event(false))
                    Log.d("tag_success", "getMyTotalNextLikedPosts: ${response.body()}")

                    if (response.body()!!.code == 1000 && response.body()!!.result.postListDTO.isNotEmpty()) {
                        _postList.postValue(response.body()!!.result.postListDTO)
                        val lastIdx = response.body()!!.result.postListDTO.lastIndex
                        _lastPostId.postValue(response.body()!!.result.postListDTO[lastIdx].postId)
                    }
                    else if (response.body()!!.code == 3031) {
                        _lastPostId.postValue(-1)
                    }
                } else {
                    Log.d("tag_fail", "getMyTotalNextLikedPosts Error: ${response.code()}")
                }
            }
        }
    }

    fun getUnivList() = viewModelScope.launch(Dispatchers.IO) {
        repository.getUnivList().onSuccess { response ->
            if (response.isSuccessful) {
                for (i in response.body()!!.result) {
                    univs[i.name]=i.univIdx
                }
                _getUnivListSuccess.postValue(Event(true))
            }
            else {
                Log.d("fail","getUniv: ${response.body()}")
            }
        }
    }

    // in school fragment, once dropdown item is clicked, then call this function
    fun setUnivId(_univId: Int) {
        // if item is clicked,
        univId = _univId
    }

    fun getUnivId() : Int{
        return univId
    }

    fun getDomain(univId : Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.getDomain(univId).onSuccess { response ->
            if (response.isSuccessful) {
                domain = response.body()!!.result[0].domain
                _getDomainSuccess.postValue(Event(true))
            }
            else {
                Log.d("fail", "getDomain: ${response.body()}")
            }
        }
    }

    fun getMemberDomain() = viewModelScope.launch(Dispatchers.IO) {
        repository.getMemberDomain().onSuccess { response ->
            if (response.isSuccessful) {
                domain = response.body()!!.result.domain
                _getDomainSuccess.postValue(Event(true))
            }
            else {
                Log.d("fail", "getDomain: ${response.body()}")
            }
        }
    }

    fun validateEmail() {
        checkEmail(email.value!!)
    }

    private fun checkEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.checkEmail(Email("$email@$domain")).onSuccess { response ->
            if (response.isSuccessful) {
                when (response.body()!!.code) {
                    2012 -> {
                        _isEmailVerified.postValue("")
                        userEmail = "$email@$domain"
                    }
                    1000 -> {
                        _isEmailVerified.postValue(EMAIL_ERROR)
                    }
                    else -> {
                        _isEmailVerified.postValue("쑤쑤")
                    }
                }
                Log.d("tag_success", response.body().toString()+"ah:"+isEmailVerified.value.toString()+"hi")
            } else {
                Log.d("tag_fail", "checkEmail Error: ${response.code()}")
            }
        }
    }

    fun sendCode() {
        _loading.postValue(Event(true))
        Log.d("email", userEmail)
        viewModelScope.launch(Dispatchers.IO) {
            Email(userEmail).let {
                repository.sendCode(it).onSuccess  { response ->
                    if (response.isSuccessful) {
                        if (response.body()!!.code == 1000) {
                            _sendCodeSuccess.postValue(Event(true))
                            _loading.postValue(Event(false))
                            Log.d("tag_success",response.body().toString())
                        } else {
                            Log.d("tag_fail", "sendCode Error: ${response.code()}")
                        }
                    } else {
                        Log.d("tag_fail", "sendCode Error: ${response.code()}")
                    }
                }
            }
        }
    }

    fun validateCode() {
        checkCode(code.value!!)
    }

    private fun checkCode(code: String) = viewModelScope.launch(Dispatchers.IO) {
        Code(userEmail,code).let {
            repository.checkCode(it).onSuccess { response ->
                if (response.isSuccessful) {
                    if (response.body()!!.code == 2013) {
                        _isCodeVerified.postValue(SignupChangepwUtils.CODE_ERROR)
                    } else if (response.body()!!.code == 3015) {
                        _isCodeVerified.postValue(SignupChangepwUtils.CODE_TIMEOUT)
                    } else if (response.body()!!.code == 1000) {
                        _isCodeVerified.postValue("")
                    } else {
                        _isCodeVerified.postValue(" ")
                    }
                    Log.d("tag_success",response.body().toString())
                } else {
                    Log.d("tag_fail", "checkCode Error: ${response.code()}")
                }
            }
        }
    }

    fun validatePw() {
        if (!SignupChangepwUtils.validate(password, SignupChangepwUtils.PW_REGEX)) {
            _isPwVerified.postValue(SignupChangepwUtils.PW_ERROR)
        } else {
            _isPwVerified.postValue("")
        }
    }

    fun validatePwConfirm() {
        if (confirmPw.get().isEmpty() || password.get() != confirmPw.get()) {
            _isPwConfirmVerified.postValue(SignupChangepwUtils.PW_CONFIRM_ERROR)
        } else {
            _isPwConfirmVerified.postValue("")
            userPw = password.value!!
        }
    }

    fun comparePw(): Boolean {
        return (password.value == confirmPw.value)
    }

    fun resetPwd() {
        _loading.postValue(Event(true))

        val user = OldUser(
            email = userEmail, pwd = userPw, ""
        )

        Log.d("tag_user", user.toString())

        viewModelScope.launch(Dispatchers.IO) {
            repository.resetPw(user).onSuccess { response ->
                if (response.isSuccessful) {
                    Log.d("tag_success", "reset: ${response.body()}")
                    if (response.body()!!.code == 1000) {
                        _resetSuccess.postValue(Event(true))
                    }
                } else {
                    Log.d("tag_fail", "reset Error: ${response.code()}")
                }
            }
        }
    }

    fun delAccount() {
        Log.d("quit",quitEmail.value+quitPassword.value)
        deleteAccount(quitEmail.value!!, quitPassword.value!!)
    }

    private fun deleteAccount(email:String, pw:String) = viewModelScope.launch(Dispatchers.IO) {
        _loading.postValue(Event(true))
        OldUser(email,pw,"").let {
            repository.deleteAccount(it).onSuccess { response ->
                if (response.isSuccessful && response.body()!!.code == 1000) {
                    _isDeleteAccount.postValue("")
                }
                else {
                    if(response.body()!!.code == 3011 || response.body()!!.code == 3016 ) {
                        _isDeleteAccount.postValue(USER_ERROR)
                    }
                    else {
                        Log.d("tag_fail", "delete Error: ${response.code()}")
                    }
                }
            }
        }
    }

    fun getServiceTerms() {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getService().onSuccess { response ->
                if (response.isSuccessful && response.body()!!.code == 1000) {
                    _isTermSuccess.postValue(Event(true))
                    terms = response.body()!!.result
                }
            }
        }
    }

    fun getPrivacyTerms() {
        _loading.postValue(Event(true))

        viewModelScope.launch(Dispatchers.IO) {
            repository.getPrivacy().onSuccess { response ->
                if (response.isSuccessful && response.body()!!.code == 1000) {
                    _isTermSuccess.postValue(Event(true))
                    terms = response.body()!!.result
                }
            }
        }
    }

}