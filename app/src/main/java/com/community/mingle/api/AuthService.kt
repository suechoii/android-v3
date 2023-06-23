package com.community.mingle.api

import com.community.mingle.service.models.AuthResponse
import com.community.mingle.service.models.Email
import com.community.mingle.service.models.FcmToken
import com.community.mingle.service.models.ResultResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthService {

    /*토큰 재발급*/
    @POST("/auth/refresh-token")
    suspend fun refresh(
        @Body email: Email,  @Header("Authorization") refreshToken: String
    ): Response<AuthResponse>
}