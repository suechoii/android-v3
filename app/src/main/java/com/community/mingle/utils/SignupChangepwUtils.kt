package com.community.mingle.utils

import androidx.lifecycle.MutableLiveData
import com.community.mingle.utils.Constants.get
import java.util.regex.Pattern

object SignupChangepwUtils {

    // 정규표현식
    const val PW_REGEX = "^(?=.*[A-Za-z])(?=.*[0-9]).{6,}\$"

    // 에러 메시지
    const val PW_ERROR = "비밀번호는 6자 이상의 영문, 숫자를 포함해야 합니다."
    const val PW_CONFIRM_ERROR = "비밀번호가 일치하지 않습니다."
    const val EMAIL_DUP = "이미 사용 중인 이메일입니다."
    const val CODE_ERROR = "인증번호가 일치하지 않습니다."
    const val CODE_TIMEOUT = "인증번호 입력시간이 초과되었습니다."
    const val NICKNAME_DUP = " 은(는) 이미 사용 중인 닉네임입니다."
    const val EMAIL_ERROR = "가입되지 않은 이메일입니다."
    const val USER_ERROR = "비밀번호/ 이메일이 틀렸습니다."

    // 정규식 검사
    fun validate(data: MutableLiveData<String>, regex: String): Boolean {
        return Pattern.compile(regex).matcher(data.get()).matches()
    }
}