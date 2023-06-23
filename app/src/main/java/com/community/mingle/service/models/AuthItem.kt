package com.community.mingle.service.models

import com.google.gson.annotations.SerializedName

data class Code(
    @SerializedName(value = "email") var email: String,
    @SerializedName(value = "code") var code: String
)

data class Reset(
    @SerializedName(value = "email") var email: String,
    @SerializedName(value = "pwd") var pwd: String
)

data class UnivListResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<UnivNameResult>
)

data class UnivNameResult(
    @SerializedName("univIdx") val univIdx: Int,
    @SerializedName("name") val name: String
)

data class UnivDomainResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<UnivDomainResult>
)

data class UnivDomainResult(
    @SerializedName("emailIdx") val emailIdx: Int,
    @SerializedName("domain") val domain: String
)
