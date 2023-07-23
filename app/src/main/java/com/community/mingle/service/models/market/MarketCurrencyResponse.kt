package com.community.mingle.service.models.market

import com.google.gson.annotations.SerializedName

data class MarketCurrencyResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<String>,
)
