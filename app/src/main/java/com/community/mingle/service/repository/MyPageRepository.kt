package com.community.mingle.service.repository

import com.community.mingle.api.MyPageService
import com.community.mingle.service.models.Code
import com.community.mingle.service.models.Email
import com.community.mingle.service.models.Nickname
import com.community.mingle.service.models.OldUser
import javax.inject.Inject

class MyPageRepository
@Inject
constructor(private val myPageService: MyPageService) {

    suspend fun logout() =
        myPageService.logout()

    suspend fun deleteAccount(user: OldUser) =
        myPageService.deleteAccount(user)

    suspend fun modifyNickname(nickname: Nickname) =
        myPageService.modifyNickname(nickname)

    suspend fun getUnivList() =
        myPageService.getUnivList()

    suspend fun getDomain(univId: Int) =
        myPageService.getDomain(univId)

    suspend fun checkEmail(email: Email) =
        myPageService.checkEmail(email)

    suspend fun sendCode(email: Email) =
        myPageService.sendCodeForReset(email)

    suspend fun checkCode(code: Code) =
        myPageService.checkCode(code)

    suspend fun resetPw(user: OldUser) =
        myPageService.resetPwd(user)

    suspend fun getService() =
        myPageService.getService()

    suspend fun getPrivacy() =
        myPageService.getPrivacy()

    suspend fun getMyUnivPost(postId: Int) =
        myPageService.getMyUnivPost(postId)

    suspend fun getMyTotalPost(postId: Int) =
        myPageService.getMyTotalPost(postId)

    suspend fun getMyUnivComment(postId: Int) =
        myPageService.getMyUnivComment(postId)

    suspend fun getMyTotalComment(postId: Int) =
        myPageService.getMyTotalComment(postId)

    suspend fun getMyLikedUniv(postId: Int) =
        myPageService.getMyLikedUniv(postId)

    suspend fun getMyLikedTotal(postId: Int) =
        myPageService.getMyLikedTotal(postId)

    suspend fun getMyUnivScrap(postId: Int) =
        myPageService.getMyUnivScrap(postId)

    suspend fun getMyTotalScrap(postId: Int) =
        myPageService.getMyTotalScrap(postId)

}