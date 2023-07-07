package com.community.mingle.service.models

import com.google.gson.annotations.SerializedName

data class CountryDTO(
    @SerializedName("countryId") val countryId: Int,
    @SerializedName("countryName") val countryName: String,
)
