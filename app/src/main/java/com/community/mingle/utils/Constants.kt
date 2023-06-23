package com.community.mingle.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import kotlin.math.roundToInt

object Constants {

    const val NETWORK_DISCONNECT = "네트워크 연결 상태를 확인해주세요."

    // 댓글 옵션
    const val DELETE_COMMENT = "삭제"
    const val REPORT_COMMENT = "신고"

    // MutableLiveData에 저장된 값 가져오기
    fun MutableLiveData<String>.get(): String {
        return this.value ?: ""
    }

    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun makeDefaultSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    fun convertDPtoPX(context: Context, dp: Int): Int {
        val density: Float = context.resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }

    fun convertPXtoDP(context: Context, px: Int): Int {
        val density: Float = context.resources.displayMetrics.density
        return (px.toFloat() / density).roundToInt()
    }

    fun getBoardName(boardType: String): String {
        return when (boardType) {
            "free" -> BoardType.FREE
            "secret" -> BoardType.SECRET
            "hot" -> BoardType.HOT
            "Information" -> BoardType.INFO
            else -> BoardType.FREE
        }
    }

}

object PostWriteType {
    const val EDIT = 1 // post 수정
    const val WRITE = 2 // post 작성
}

object BoardType {
    const val FREE = "자유게시판"
    const val SECRET = "비밀게시판"
    const val HOT = "인기게시판"
    const val INFO = "정보게시판"
}

object UserPostType {
    const val MY_POST = "내가 작성한 글"
    const val MY_COMMENT_POST = "내가 작성한 댓글"
    const val MY_SCRAP_POST = "스크랩한 글"
    const val MY_LIKE_POST = "좋아요 누른 글"
    const val MARKET_SELL = "판매내역"
    const val MY_ITEM = "찜한내역"
    const val SERVICE_TERMS = "이용약관"
    const val PRIVACY_TERMS = "개인정보 처리방침"
    const val CHANGE_NICKNAME = "닉네임 변경"
    const val CHANGE_PASSWORD = "비밀번호 변경"
    const val ENQUIRE_MINGLE = "밍글에 문의하기 "
    const val LEAVE = "탈퇴하기"
}