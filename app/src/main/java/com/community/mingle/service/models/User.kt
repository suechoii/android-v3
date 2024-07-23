package com.community.mingle.service.models

import com.google.gson.annotations.SerializedName

data class SignUpResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,    //성공 여부
    @SerializedName("code") val code: Int,  //code 호출
    @SerializedName("message") val message: String, //메세지
    @SerializedName("result") val result: SignUpResult?
)

data class SignUpResult(
    @SerializedName("memberId") val memberId: Int
)

data class NewUser (
    @SerializedName(value = "univId") val univId : Int,
    @SerializedName(value = "email") val email : String,
    @SerializedName(value = "pwd") val pwd : String,
    @SerializedName(value = "nickname") val nickname : String
)

data class LoginResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,    //성공 여부
    @SerializedName("code") val code: Int,  //code 호출
    @SerializedName("message") val message: String, //메세지
    @SerializedName("result") val result: LoginResult?
)

data class LoginResult(
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("hashedEmail") val hashedEmail: String,
    @SerializedName("nickName") val nickName: String,
    @SerializedName("univName") val univName: String,
    @SerializedName("country") val country: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class OldUser(
    @SerializedName(value = "email") var email: String,
    @SerializedName(value = "password") var pwd: String,
    @SerializedName(value = "fcmToken") var fcmToken: String
)

data class Email(
    @SerializedName(value = "email") var email: String
)

data class Nickname (
    @SerializedName(value = "nickname") var nickname: String
)

data class FcmToken(
    @SerializedName(value = "fcmToken") var fcmToken: String
)



