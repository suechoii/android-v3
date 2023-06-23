package com.community.mingle.service.repository

import com.community.mingle.api.MemberService
import com.community.mingle.service.models.*
import javax.inject.Inject

/* 로그인, 회원가입 Repository */

class MemberRepository
@Inject
constructor(private val memberService: MemberService) {

    suspend fun login(oldUser: OldUser) =
        memberService.login(oldUser)

    suspend fun signup(newUser: NewUser) =
        memberService.signUp(newUser)

    suspend fun getUnivList() =
        memberService.getUnivList()

    suspend fun getDomain(univId: Int) =
        memberService.getDomain(univId)

    suspend fun checkEmail(email: Email) =
        memberService.checkEmail(email)

    suspend fun sendCode(email: Email) =
        memberService.sendCode(email)

    suspend fun checkCode(code: Code) =
        memberService.checkCode(code)

    suspend fun getServiceTerms() =
        memberService.getService()

    suspend fun getPrivacyTerms() =
        memberService.getPrivacy()

    suspend fun fcmRefresh(fcmToken: FcmToken) =
        memberService.fcmRefresh(fcmToken)
}
