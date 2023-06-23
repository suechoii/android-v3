package com.community.mingle.service.models

import com.google.gson.annotations.SerializedName

data class HomeListResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<HomeResult>
)

data class HomeResult(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("likeCount") val likeCount: String,
    @SerializedName("commentCount") val commentCount: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("blinded") var blinded: Boolean,
    @SerializedName("fileAttached") val fileAttached: Boolean,
    @SerializedName("reported") val reported: Boolean
)

class BannerResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<Banner>
)

data class Banner (
    @SerializedName("id") val id: Int,
    @SerializedName("url") val url: String,
    @SerializedName("link") val link: String?
)

data class Notifications(
    @SerializedName(value = "boardType") var boardType: String,
    @SerializedName(value = "notificationId") var notificationId: Int
)

data class NotificationResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<NotiData>
)
data class NotiData(
    @SerializedName("notificationId") val notificationId: Int,
    @SerializedName("postId") val postId: Int,
    @SerializedName("notificationType") val notificationType: String,
    @SerializedName("content") val content: String,
    @SerializedName("boardType") val boardType: String,
    @SerializedName("category") val category: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("read") val read: Boolean
)
