package com.community.mingle.service.models

import com.google.gson.annotations.SerializedName

data class CountryListResponse(
    @SerializedName("isSuccess")val isSuccess:Boolean,
    @SerializedName("code")val code:Int,
    @SerializedName("message")val message:String,
    @SerializedName("result")val result:List<CountryDTO>
)