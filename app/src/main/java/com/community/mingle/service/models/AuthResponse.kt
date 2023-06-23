package com.community.mingle.service.models

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,    //성공 여부
    @SerializedName("code") val code: Int,  //code 호출
    @SerializedName("message") val message: String, //메세지
    @SerializedName("result") val result: Result
)

data class Result(
    @SerializedName("memberId") val memberId: Int,
    @SerializedName("email") val email: String,
    @SerializedName("nickName") val nickName: String,
    @SerializedName("univName") val univName: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)