package com.community.mingle.api

import com.community.mingle.service.models.AuthResponse
import com.community.mingle.service.models.Email
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {

    /*토큰 재발급*/
    @POST("/auth/refresh-token")
    suspend fun refresh(
        @Body email: Email,  @Header("Authorization") refreshToken: String
    ): Response<AuthResponse>
}