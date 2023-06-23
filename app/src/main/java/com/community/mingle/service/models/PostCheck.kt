package com.community.mingle.service.models

import com.google.gson.annotations.SerializedName

/* 댓글 리스트 */
data class CommentList(
    @SerializedName("code") val code: Int,
    @SerializedName("result") val result: List<Comment2>
)

/* 댓글 */
data class Comment2(
    @SerializedName("commentId") val commentId: Int,
    @SerializedName("nickname") var nickname: String,
    @SerializedName("content") var content: String,
    @SerializedName("likeCount") var likeCount: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("coCommentsList") val coCommentsList: ArrayList<Reply>?,
    @SerializedName("liked") var liked: Boolean,
    @SerializedName("commentFromAuthor") val commentFromAuthor : Boolean,
    @SerializedName("myComment") val myComment: Boolean,
    @SerializedName("commentDeleted") var commentDeleted: Boolean,
    @SerializedName("commentReported") val commentReported: Boolean,
    @SerializedName("admin") val admin: Boolean
)

/* 대댓글 */
data class Reply(
    @SerializedName("commentId") val commentId: Int,
    @SerializedName("parentCommentId") val parentCommentId: Int,
    @SerializedName("mention") val mention: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("content") val content: String,
    @SerializedName("likeCount") var likeCount: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("liked") var liked: Boolean,
    @SerializedName("commentFromAuthor") val commentFromAuthor : Boolean,
    @SerializedName("myComment") val myComment: Boolean,
    @SerializedName("commentDeleted") val commentDeleted: Boolean,
    @SerializedName("commentReported") val commentReported: Boolean,
    @SerializedName("admin") val admin: Boolean
)

/* 보내는 댓글 */
data class CommentSend(
    @SerializedName("isAnonymous")
    val isAnonymous: Boolean,
    @SerializedName("content")
    val content: String,
    @SerializedName("mentionId")
    val mentionId: Nothing? = null,
    @SerializedName("parentCommentId")
    val parentCommentId: Nothing? = null,
    @SerializedName("postId")
    val postId: Int
)

/* 보내는 대댓글 */
data class ReplySend(
    @SerializedName("isAnonymous")
    val isAnonymous: Boolean,
    @SerializedName("content")
    val content: String,
    @SerializedName("mentionId")
    val mentionId: Int,
    @SerializedName("parentCommentId")
    val parentCommentId: Int,
    @SerializedName("postId")
    val postId: Int
)