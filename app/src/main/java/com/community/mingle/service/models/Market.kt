package com.community.mingle.service.models

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.net.URL

class MarketListResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: MarketDTOResult
)

data class MarketDTOResult(
    @SerializedName("name") val name: String?,
    @SerializedName("itemListDTO") val itemListDTO: List<MarketPostResult>
)

data class MarketPostResult(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("price") val price: String,
    @SerializedName("nickName") val nickName: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("likeCount") var likeCount: String,
    @SerializedName("commentCount") val commentCount: String,
    @SerializedName("status") val status: String,
    @SerializedName("imgThumbnailUrl") val imgThumbnailUrl: URL,
    @SerializedName("liked") var liked: Boolean,
    @SerializedName("content") var content: String,
    @SerializedName("location") var location: String,
    @SerializedName("itemImgList") var itemImgList: ArrayList<URL>,
    @SerializedName("chatUrl") var chatUrl: String
)

data class MarketItemDetailResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ItemDetail
)

data class ItemDetail(
    @SerializedName("itemId") val itemId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("price") val price: String,
    @SerializedName("location") val location: String,
    @SerializedName("chatUrl") val chatUrl: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("likeCount") val likeCount: String,
    @SerializedName("commentCount") var commentCount: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("viewCount") val viewCount: String,
    @SerializedName("postImgUrl") val postImgUrl: java.util.ArrayList<URL>,
    @SerializedName("status") val status: String,
    @SerializedName("myPost") val myPost: Boolean,
    @SerializedName("liked") val liked: Boolean,
    @SerializedName("blinded") val blinded: Boolean,
    @SerializedName("fileAttached") val fileAttached: Boolean,
    @SerializedName("reported") val reported: Boolean,
    @SerializedName("admin") val admin: Boolean
)

/* 보내는 댓글 */
data class MarketCommentSend(
    @SerializedName("itemId")
    val itemId: Int,
    @SerializedName("parentCommentId")
    val parentCommentId: Nothing? = null,
    @SerializedName("mentionId")
    val mentionId: Nothing? = null,
    @SerializedName("content")
    val content: String,
    @SerializedName("isAnonymous")
    val isAnonymous: Boolean
)

/* 보내는 대댓글 */
data class MarketReplySend(
    @SerializedName("itemId")
    val itemId: Int,
    @SerializedName("parentCommentId")
    val parentCommentId: Int,
    @SerializedName("mentionId")
    val mentionId: Int,
    @SerializedName("content")
    val content: String,
    @SerializedName("isAnonymous")
    val isAnonymous: Boolean
)

data class MarketWritePostResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: WritePost
)

data class WritePost (
    @SerializedName("itemId") val itemId: Int
)