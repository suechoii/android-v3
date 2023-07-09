package com.community.mingle.service.repository

import com.community.mingle.api.MemberService
import com.community.mingle.model.user.Country
import com.community.mingle.service.models.Code
import com.community.mingle.service.models.Email
import com.community.mingle.service.models.FcmToken
import com.community.mingle.service.models.LoginResponse
import com.community.mingle.service.models.NewUser
import com.community.mingle.service.models.OldUser
import com.community.mingle.service.models.ResultResponse
import com.community.mingle.service.models.SignUpResponse
import com.community.mingle.service.models.UnivDomainResponse
import com.community.mingle.service.models.UnivListResponse
import retrofit2.Response
import javax.inject.Inject

/* 로그인, 회원가입 Repository */

class MemberRepository
@Inject
constructor(private val memberService: MemberService) {

    suspend fun login(oldUser: OldUser): Result<Response<LoginResponse>> = runCatching {
        memberService.login(oldUser)
    }

    suspend fun signup(newUser: NewUser): Result<Response<SignUpResponse>> = runCatching {
        memberService.signUp(newUser)
    }

    suspend fun getUnivListByCountryId(countryId: Int): Result<Response<UnivListResponse>> = runCatching {
        memberService.getUnivList(countryId)
    }

    suspend fun getDomain(univId: Int): Result<Response<UnivDomainResponse>> = runCatching {
        memberService.getDomain(univId)
    }

    suspend fun checkEmail(email: Email): Result<Response<ResultResponse>> = runCatching {
        memberService.checkEmail(email)
    }

    suspend fun sendCode(email: Email): Result<Response<ResultResponse>> = runCatching {
        memberService.sendCode(email)
    }

    suspend fun checkCode(code: Code): Result<Response<ResultResponse>> = runCatching {
        memberService.checkCode(code)
    }

    suspend fun getServiceTerms(): Result<Response<ResultResponse>> = runCatching {
        memberService.getService()
    }

    suspend fun getPrivacyTerms(): Result<Response<ResultResponse>> = runCatching {
        memberService.getPrivacy()
    }

    suspend fun fcmRefresh(fcmToken: FcmToken): Result<Response<ResultResponse>> = runCatching {
        memberService.fcmRefresh(fcmToken)
    }

    suspend fun getCountryList(): Result<List<Country>> = runCatching {
        memberService.getCountryList()
            .let { response ->
                if (response.isSuccess) {
                    response.result.map {
                        Country(
                            id = it.countryId,
                            name = it.countryName,
                        )
                    }
                } else {
                    throw IllegalStateException(response.message)
                }
            }
    }
}
