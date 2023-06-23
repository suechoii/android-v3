package com.community.mingle.api

import com.community.mingle.service.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface MyPageService {

    /* 로그아웃 */
    @POST("/member/logout")
    suspend fun logout(
    ) : Response<ResultResponse>

    /* 회원 탈퇴 */
    @PATCH("/member/delete")
    suspend fun deleteAccount(
        @Body user: OldUser
    ) : Response<ResultResponse>

    /* 닉네임 변경 */
    @PATCH("/member/nickname")
    suspend fun modifyNickname(
        @Body nickname: Nickname
    ): Response<ResultResponse>

    /* 대학교 리스트 가져오기 */
    @GET("/auth/univlist")
    suspend fun getUnivList(
    ): Response<UnivListResponse>

    /* 대학교 별 이메일 도메인 리스트 가져오기 */
    @GET("/auth/domain")
    suspend fun getDomain(
        @Query("univId") univId: Int
    ): Response<UnivDomainResponse>

    /* 이메일 입력 & 존재하는 유저인지 체크 */
    @POST("/auth/checkemail")
    suspend fun checkEmail(
        @Body email: Email
    ): Response<ResultResponse>

    /* 비밀번호 재설정 인증번호 전송 */
    @POST("/auth/sendcode/pwd")
    suspend fun sendCodeForReset(
        @Body email: Email
    ): Response<ResultResponse>

    /* 이메일 인증코드 검사 */
    @POST("/auth/checkcode")
    suspend fun checkCode(
        @Body code: Code
    ): Response<ResultResponse>

    /* 비밀번호 재설정 */
    @PATCH("/auth/pwd")
    suspend fun resetPwd(
        @Body user: OldUser
    ) : Response<ResultResponse>

    /* 운영정책 불러오기 */
    @GET("/auth/terms/service")
    suspend fun getService(
    ): Response<ResultResponse>

    @GET("/auth/terms/privacy")
    suspend fun getPrivacy(
    ): Response<ResultResponse>

    /* 내가 쓴 학교 게시물 조회 */
    @GET("/member/posts/univ")
    suspend fun getMyUnivPost(
        @Query("postId") postId: Int
    ) : Response<PostListResponse>

    /* 내가 쓴 통합 게시물 조회 */
    @GET("/member/posts/total")
    suspend fun getMyTotalPost(
        @Query("postId") postId: Int
    ) : Response<PostListResponse>

    /* 내가 쓴 학교 댓글 조회 */
    @GET("/member/comments/univ")
    suspend fun getMyUnivComment(
        @Query("postId") postId: Int
    ) : Response<PostListResponse>

    /* 내가 쓴 통합 댓글 조회 */
    @GET("/member/comments/total")
    suspend fun getMyTotalComment(
        @Query("postId") postId: Int
    ) : Response<PostListResponse>

    /* 내가 좋아요 누른 학교 글 조회 */
    @GET("/member/likes/univ")
    suspend fun getMyLikedUniv(
        @Query("postId") postId: Int
    ) : Response<PostListResponse>

    /* 내가 좋아요 누른 통합 글 조회 */
    @GET("/member/likes/total")
    suspend fun getMyLikedTotal(
        @Query("postId") postId: Int
    ) : Response<PostListResponse>

    /* 내가 스크랩한 학교 게시물 */
    @GET("/member/scraps/univ")
    suspend fun getMyUnivScrap(
        @Query("postId") postId: Int
    ) : Response<PostListResponse>

    /* 내가 스크랩한 통합 게시물 */
    @GET("/member/scraps/total")
    suspend fun getMyTotalScrap(
        @Query("postId") postId: Int
    ) : Response<PostListResponse>

}