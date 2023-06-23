package com.community.mingle.service.repository

import com.community.mingle.api.AuthService
import com.community.mingle.service.models.Email
import com.community.mingle.service.models.FcmToken
import javax.inject.Inject

class AuthRepository
@Inject
constructor(private val authService: AuthService) {
    suspend fun refresh(email: Email, refreshToken: String) =
        authService.refresh(email, refreshToken)
}