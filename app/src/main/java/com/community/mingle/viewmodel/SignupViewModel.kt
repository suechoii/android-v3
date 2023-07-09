package com.community.mingle.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.community.mingle.model.user.Country
import com.community.mingle.service.models.Code
import com.community.mingle.service.models.Email
import com.community.mingle.service.models.NewUser
import com.community.mingle.service.repository.MemberRepository
import com.community.mingle.utils.Constants.get
import com.community.mingle.utils.Event
import com.community.mingle.utils.SignupChangepwUtils.CODE_ERROR
import com.community.mingle.utils.SignupChangepwUtils.CODE_TIMEOUT
import com.community.mingle.utils.SignupChangepwUtils.EMAIL_DUP
import com.community.mingle.utils.SignupChangepwUtils.NICKNAME_DUP
import com.community.mingle.utils.SignupChangepwUtils.PW_CONFIRM_ERROR
import com.community.mingle.utils.SignupChangepwUtils.PW_ERROR
import com.community.mingle.utils.SignupChangepwUtils.PW_REGEX
import com.community.mingle.utils.SignupChangepwUtils.validate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.List
import kotlin.collections.emptyList
import kotlin.collections.mutableMapOf
import kotlin.collections.set

@HiltViewModel
class SignupViewModel
@Inject
constructor(
    private val repository: MemberRepository,
) : ViewModel() {

    private val _toastEventFlow = MutableSharedFlow<String>()
    val toastEventFlow = _toastEventFlow.asSharedFlow()
    private val _selectableCountryList = MutableStateFlow<List<Country>>(emptyList())
    val selectableCountryList = _selectableCountryList.asStateFlow()
    private val _countryDropDownListShow = MutableStateFlow(false)
    val countryDropDownListShow = _countryDropDownListShow.asStateFlow()
    private val _refreshCountryListVisible = MutableStateFlow(false)
    val refreshCountryListVisible = _refreshCountryListVisible.asStateFlow()
    private val _selectedCountry = MutableStateFlow<Country?>(null)
    val selectedCountry = _selectedCountry.asStateFlow()
    val nextButtonInCountrySelectionEnabled = _selectedCountry.map { it != null }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
    val univs = mutableMapOf<String, Int>()

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

    // nickname
    val nickname: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    // loading
    private val _loading = MutableLiveData<Event<Boolean>>()
    val loading: LiveData<Event<Boolean>> = _loading

    // 각 항목별 verified 여부
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
    private val _isNicknameError = MutableLiveData<String>()
    val isNicknameError: LiveData<String> get() = _isNicknameError

    // api call success 여부
    private val _getUnivListSuccess = MutableLiveData<Event<Boolean>>()
    val getUnivListSuccess: LiveData<Event<Boolean>> = _getUnivListSuccess
    private val _getDomainSuccess = MutableLiveData<Boolean>()
    val getDomainSuccess: LiveData<Boolean> = _getDomainSuccess
    private val _sendCodeSuccess = MutableLiveData<Event<Boolean>>()
    val sendCodeSuccess: LiveData<Event<Boolean>> = _sendCodeSuccess
    private val _getTermsSuccess = MutableLiveData<Event<Boolean>>()
    val getTermsSuccess: LiveData<Event<Boolean>> = _getTermsSuccess
    private val _signupSuccess = MutableLiveData<Event<Boolean>>()
    val signupSuccess: LiveData<Event<Boolean>> = _signupSuccess
    private var univId: Int = 0
    private var userEmail = ""
    var domain = ""
    private var userPw = ""
    var terms = ""
    var termsTitle = ""
    private var userName = ""

    init {
        getCountryList()
    }

    // 회원가입 단계별 함수

    fun refreshCountryList() {
        _refreshCountryListVisible.value = false
        getCountryList()
    }
    private fun getCountryList() {
        viewModelScope.launch {
            repository.getCountryList()
                .onSuccess { list ->
                    _selectableCountryList.value = list
                    _refreshCountryListVisible.value = false
                }.onFailure {
                    _toastEventFlow.emit("국가 목록을 불러오는데 실패했습니다. 새로고침 후 다시 시도해주세요.")
                    _refreshCountryListVisible.value = true
                }
        }
    }

    fun toggleCountryDropDownShown() {
        _countryDropDownListShow.update { !it }
    }

    fun selectCountryByName(countryName: String) {
        _selectedCountry.value = selectableCountryList.value.find { it.name == countryName }
    }

    fun getUnivList() = viewModelScope.launch(Dispatchers.IO) {
        val countryId = selectedCountry.value?.id ?: return@launch
        repository.getUnivListByCountryId(countryId).onSuccess { response ->
            if (response.isSuccessful) {
                for (i in response.body()!!.result) {
                    univs[i.name] = i.univIdx
                }
                _getUnivListSuccess.postValue(Event(true))
            } else {
                Log.d("fail", "getUniv: ${response.body()}")
            }
        }
    }

    fun getDomain(univId: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.getDomain(univId).onSuccess { response ->
            if (response.isSuccessful && response.body()!!.code == 1000) {
                domain = response.body()!!.result[0].domain
                _getDomainSuccess.postValue(true)
            } else {
                Log.d("fail", "getDomain: ${response.body()}")
            }
        }
    }

    // in school fragment, once dropdown item is clicked, then call this function
    fun setUnivId(_univId: Int) {
        // if item is clicked,
        univId = _univId
    }

    fun getUnivId(): Int {
        return univId
    }

    // used in step2 fragment
    fun validateEmail() {
        checkEmail(email.value!!)
    }

    private fun checkEmail(email: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.checkEmail(Email("$email@$domain")).onSuccess { response ->
            if (response.isSuccessful) {
                when (response.body()!!.code) {
                    2012 -> {
                        _isEmailVerified.postValue(EMAIL_DUP)
                    }

                    1000 -> {
                        _isEmailVerified.postValue("")
                    }

                    else -> {
                        _isEmailVerified.postValue(" ")
                    }
                }
                userEmail = "$email@$domain"
                Log.d("tag_success", response.body().toString())
            } else {
                Log.d("tag_fail", "checkEmail Error: ${response.code()}")
            }
        }
    }

    fun sendCode() = viewModelScope.launch(Dispatchers.IO) {
        _loading.postValue(Event(true))

        repository.sendCode(Email(userEmail)).onSuccess { response ->
            if (response.isSuccessful) {
                _loading.postValue(Event(false))
                if (response.body()!!.code == 1000) {
                    _sendCodeSuccess.postValue(Event(true))
                    Log.d("tag_success", response.body().toString())
                } else {
                    Log.d("tag_fail", "sendCode Error: ${response.code()}")
                }
            } else {
                Log.d("tag_fail", "sendCode Error: ${response.code()}")
            }
        }
    }

    fun validateCode() {
        checkCode(code.value!!)
    }

    private fun checkCode(code: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.checkCode(Code(userEmail, code)).onSuccess { response ->
            if (response.isSuccessful) {
                if (response.body()!!.code == 2013) {
                    _isCodeVerified.postValue(CODE_ERROR)
                } else if (response.body()!!.code == 3015) {
                    _isCodeVerified.postValue(CODE_TIMEOUT)
                } else if (response.body()!!.code == 1000) {
                    _isCodeVerified.postValue("")
                } else {
                    _isCodeVerified.postValue(" ")
                }
                Log.d("tag_success", response.body().toString())
            } else {
                Log.d("tag_fail", "checkCode Error: ${response.code()}")
            }
        }
    }

    fun validatePw() {
        if (!validate(password, PW_REGEX)) {
            _isPwVerified.postValue(PW_ERROR)
        } else {
            _isPwVerified.postValue("")
        }
    }

    fun validatePwConfirm() {
        if (confirmPw.get().isEmpty() || password.get() != confirmPw.get()) {
            _isPwConfirmVerified.postValue(PW_CONFIRM_ERROR)
        } else {
            _isPwConfirmVerified.postValue("")
            userPw = password.value!!
        }
    }

    fun comparePw(): Boolean {
        return (password.value == confirmPw.value)
    }

    fun getTerms(isService: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        if (isService) {
            repository.getServiceTerms().onSuccess { response ->
                if (response.isSuccessful) {
                    if (response.body()!!.code == 1000) {
                        terms = response.body()!!.result
                        termsTitle = "서비스 이용약관"
                        _getTermsSuccess.postValue(Event(true))
                    } else {
                        Log.d("tag_fail", "getTerms Error: ${response.body()}")
                    }
                } else {
                    Log.d("tag_fail", "getTerms Error: ${response.code()}")
                }
            }
        } else {
            repository.getPrivacyTerms().onSuccess { response ->
                if (response.isSuccessful) {
                    if (response.body()!!.code == 1000) {
                        terms = response.body()!!.result
                        termsTitle = "개인정보 처리방침"
                        _getTermsSuccess.postValue(Event(true))
                    } else {
                        Log.d("tag_fail", "getTerms Error: ${response.code()}")
                    }
                } else {
                    Log.d("tag_fail", "getTerms Error: ${response.code()}")
                }
            }
        }
    }

    fun signup() {
        _loading.postValue(Event(true))

        userName = nickname.value!!
        val user = NewUser(
            univId = univId, email = userEmail, pwd = userPw, nickname = userName
        )

        Log.d("tag_user", user.toString())

        viewModelScope.launch(Dispatchers.IO) {
            repository.signup(user).onSuccess { response ->
                if (response.isSuccessful && response.body()!!.code == 1000) {
                    Log.d("tag_success", "signup: ${response.body()}")
                    _signupSuccess.postValue(Event(true))
                } else {
                    if (response.body()!!.code == 2017) {
                        _isNicknameError.postValue(nickname.get() + NICKNAME_DUP)
                    }
                    Log.d("tag_fail", "signup Error: ${response.code()}")
                }
            }
        }
    }

}