package com.community.mingle.service.models

import com.community.mingle.model.post.PostType
import com.google.gson.annotations.SerializedName

class PostListResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: DTOResult
)

data class DTOResult(
    @SerializedName("boardName") val boardName: String?,
    @SerializedName("postListDTO") val postListDTO: List<PostResult>
)

data class PostResult(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("contents") val contents: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("likeCount") val likeCount: String,
    @SerializedName("boardType") val boardType: String? = null,
    @SerializedName("commentCount") val commentCount: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("blinded") var blinded: Boolean,
    @SerializedName("fileAttached") val fileAttached: Boolean,
    @SerializedName("reported") val reported: Boolean
): PostListItem

sealed interface PostListItem {
    object Loading: PostListItem
    object NoMorePost: PostListItem
}