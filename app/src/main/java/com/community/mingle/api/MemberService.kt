package com.community.mingle.api

import com.community.mingle.service.models.*
import retrofit2.Response
import retrofit2.http.*

interface MemberService {

    /* 회원가입 */
    @POST("/auth/signup")
    suspend fun signUp(
        @Body newUser: NewUser
    ): Response<SignUpResponse>

    /* 이메일 인증코드 전송 */
    @POST("/auth/sendcode")
    suspend fun sendCode(
        @Body email: Email
    ): Response<ResultResponse>

    /* 로그인 */
    @POST("auth/login")
    suspend fun login(
        @Body oldUser: OldUser
    ) : Response<LoginResponse>

    /* 이메일 입력 & 중복검사 */
    @POST("/auth/checkemail")
    suspend fun checkEmail(
        @Body email: Email
    ): Response<ResultResponse>

    /* 이메일 인증코드 검사 */
    @POST("/auth/checkcode")
    suspend fun checkCode(
        @Body code: Code
    ): Response<ResultResponse>

    /* 비밀번호 초기화 */
    @PATCH("/auth/pwd")
    suspend fun resetPwd(
        @Body reset: Reset
    ): Response<ResultResponse>

    /* 대학교 리스트 가져오기 */
    @GET("/auth/univlist")
    suspend fun getUnivList(
    ): Response<UnivListResponse>

    /* 서비스이용약관 가져오기 */
    @GET("/auth/terms/service")
    suspend fun getService(
    ): Response<ResultResponse>

    /* 개인정보처리방침 가져오기 */
    @GET("/auth/terms/privacy")
    suspend fun getPrivacy(
    ): Response<ResultResponse>

    /* 대학교 별 이메일 도메인 리스트 가져오기 */
    @GET("/auth/domain")
    suspend fun getDomain(
        @Query("univId") univId: Int
    ): Response<UnivDomainResponse>

    /*fcm 토큰 재발급*/
    @PATCH("/auth/fcmtoken")
    suspend fun fcmRefresh(
        @Body fcmToken: FcmToken
    ) : Response<ResultResponse>

    @GET("/auth/country-list")
    suspend fun getCountryList(
    ): CountryListResponse
}