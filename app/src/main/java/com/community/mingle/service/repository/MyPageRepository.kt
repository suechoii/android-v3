package com.community.mingle.service.repository

import com.community.mingle.api.MyPageService
import com.community.mingle.service.models.Code
import com.community.mingle.service.models.Email
import com.community.mingle.service.models.Nickname
import com.community.mingle.service.models.OldUser
import com.community.mingle.service.models.PostListResponse
import com.community.mingle.service.models.ResultResponse
import com.community.mingle.service.models.UnivDomainResponse
import com.community.mingle.service.models.UnivListResponse
import retrofit2.Response
import javax.inject.Inject

class MyPageRepository
@Inject
constructor(private val myPageService: MyPageService) {

    suspend fun logout() =
        myPageService.logout()

    suspend fun deleteAccount(user: OldUser): Result<Response<ResultResponse>> = runCatching {
        myPageService.deleteAccount(user)
    }

    suspend fun modifyNickname(nickname: Nickname): Result<Response<ResultResponse>> = runCatching {
        myPageService.modifyNickname(nickname)
    }

    suspend fun getUnivList(): Result<Response<UnivListResponse>> = runCatching {
        myPageService.getUnivList()
    }

    suspend fun getDomain(univId: Int): Result<Response<UnivDomainResponse>> = runCatching {
        myPageService.getDomain(univId)
    }

    suspend fun checkEmail(email: Email): Result<Response<ResultResponse>> = runCatching {
        myPageService.checkEmail(email)
    }

    suspend fun sendCode(email: Email): Result<Response<ResultResponse>> = runCatching {
        myPageService.sendCodeForReset(email)
    }
    suspend fun checkCode(code: Code): Result<Response<ResultResponse>> = runCatching {
        myPageService.checkCode(code)
    }

    suspend fun resetPw(user: OldUser): Result<Response<ResultResponse>> = runCatching {
        myPageService.resetPwd(user)
    }

    suspend fun getService(): Result<Response<ResultResponse>> = runCatching {
        myPageService.getService()
    }

    suspend fun getPrivacy(): Result<Response<ResultResponse>> = runCatching {
        myPageService.getPrivacy()
    }

    suspend fun getMyUnivPost(postId: Int): Result<Response<PostListResponse>> = runCatching {
        myPageService.getMyUnivPost(postId)
    }

    suspend fun getMyTotalPost(postId: Int): Result<Response<PostListResponse>> = runCatching {
        myPageService.getMyTotalPost(postId)
    }

    suspend fun getMyUnivComment(postId: Int): Result<Response<PostListResponse>> = runCatching {
        myPageService.getMyUnivComment(postId)
    }

    suspend fun getMyTotalComment(postId: Int): Result<Response<PostListResponse>> = runCatching {
        myPageService.getMyTotalComment(postId)
    }

    suspend fun getMyLikedUniv(postId: Int): Result<Response<PostListResponse>> = runCatching {
        myPageService.getMyLikedUniv(postId)
    }

    suspend fun getMyLikedTotal(postId: Int): Result<Response<PostListResponse>> = runCatching {
        myPageService.getMyLikedTotal(postId)
    }

    suspend fun getMyUnivScrap(postId: Int): Result<Response<PostListResponse>> = runCatching {
        myPageService.getMyUnivScrap(postId)
    }

    suspend fun getMyTotalScrap(postId: Int): Result<Response<PostListResponse>> = runCatching {
        myPageService.getMyTotalScrap(postId)
    }

}