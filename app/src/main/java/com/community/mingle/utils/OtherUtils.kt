package com.community.mingle.utils

import androidx.lifecycle.MutableLiveData
import com.community.mingle.utils.Constants.get
import java.util.regex.Pattern

object OtherUtils {

    // 정규표현식
    const val LINK_REGEX = "^https://open\\.kakao\\.com\\/.*$"

    // 에러 메시지
    const val LINK_ERROR = "올바른 형식의 오픈채팅방 링크를 입력해주세요"

    // 정규식 검사
    fun validate(data: MutableLiveData<String>, regex: String): Boolean {
        return Pattern.compile(regex).matcher(data.get()).matches()
    }
}