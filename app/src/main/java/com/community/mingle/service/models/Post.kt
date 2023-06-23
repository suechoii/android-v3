package com.community.mingle.service.models

import com.google.gson.annotations.SerializedName
import okhttp3.RequestBody
import java.net.URL

data class PostDetailResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: PostDetail
)

data class PostDetail(
    @SerializedName("postId") val postId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("likeCount") val likeCount: String,
    @SerializedName("scrapCount") val scrapCount: Int,
    @SerializedName("commentCount") var commentCount: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("viewCount") val viewCount: String,
    @SerializedName("postImgUrl") val postImgUrl: java.util.ArrayList<URL>,
    @SerializedName("myPost") val myPost: Boolean,
    @SerializedName("liked") val liked: Boolean,
    @SerializedName("scraped") val scraped: Boolean,
    @SerializedName("blinded") val blinded: Boolean,
    @SerializedName("fileAttached") val fileAttached: Boolean,
    @SerializedName("reported") val reported: Boolean
)

data class Edit (
    @SerializedName(value = "title") var title: String,
    @SerializedName(value = "content") var content: String
)

data class LikeResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: LikeDetail
)

data class LikeDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("likeCount") val likeCount: String
)

class ScrapResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ScrapDetail
)

data class ScrapDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("scrapCount") val scrapCount: Int
)

data class ReportPost (
    @SerializedName(value = "tableType") var tableType: String,
    @SerializedName(value = "contentId") var contentId: Int,
    @SerializedName(value = "reportTypeId") var reportTypeId: Int
)

data class ReportResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ReportResult
)

data class ReportResult (
    @SerializedName("reportId") val reportId : Int
)

data class CommentResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<Comment>
)

data class Comment(
    @SerializedName("commentId") val commentId: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("content") val content: String,
    @SerializedName("likeCount") var likeCount: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("coCommentsList") val coCommentsList: ArrayList<Comment>?,
    @SerializedName("liked") var liked: Boolean,
    @SerializedName("commentFromAuthor") val commentFromAuthor : Boolean,
    @SerializedName("myComment") val myComment: Boolean,
    @SerializedName("commentDeleted") val commentDeleted: Boolean,
    @SerializedName("commentReported") val commentReported: Boolean
)

//data class Reply(
//    @SerializedName("commentId") val commentId: Int,
//    @SerializedName("parentCommentId") val parentCommentId: Int,
//    @SerializedName("mention") val mention: String,
//    @SerializedName("nickname") val nickname: String,
//    @SerializedName("content") val content: String,
//    @SerializedName("likeCount") val likeCount: String,
//    @SerializedName("createdAt") val createdAt: String,
//    @SerializedName("liked") val liked: Boolean,
//    @SerializedName("commentFromAuthor") val commentFromAuthor : Boolean,
//    @SerializedName("myComment") val myComment: Boolean,
//    @SerializedName("commentDeleted") val commentDeleted: Boolean,
//    @SerializedName("commentReported") val commentReported: Boolean
//)

data class PostComment (
    @SerializedName(value = "postId") var postId: Int,
    @SerializedName(value = "parentCommentId") var parentCommentId: Int?,
    @SerializedName(value = "mentionId") var mentionId: Int?,
    @SerializedName(value = "content") var content: String,
    @SerializedName(value = "isAnonymous") var anonymous: Boolean
)

data class PostCommentResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: CommentResult
)

data class CommentResult (
    @SerializedName("commentId") val commentId : Int,
    @SerializedName("nickname") val nickname : String,
    @SerializedName("createdAt") val createdAt : String,
    @SerializedName("commentFromAuthor") val commentFromAuthor : Boolean,
)

data class WritePostResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: WritePostResult
)

data class WritePostResult (
    @SerializedName("id") val postId : Int,
    @SerializedName("fileNameList") val fileNameList : ArrayList<String>
)

data class CategoryResponse (
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<Category>
)

data class Category (
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("categoryName") val categoryName: String
)