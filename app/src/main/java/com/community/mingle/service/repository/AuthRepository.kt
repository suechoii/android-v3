package com.community.mingle.service.repository

import com.community.mingle.api.AuthService
import com.community.mingle.service.models.AuthResponse
import com.community.mingle.service.models.Email
import retrofit2.Response
import javax.inject.Inject

class AuthRepository
@Inject
constructor(private val authService: AuthService) {

    suspend fun refresh(email: Email, refreshToken: String): Result<Response<AuthResponse>> {
        return runCatching {
            authService.refresh(email, refreshToken)
        }
    }
}