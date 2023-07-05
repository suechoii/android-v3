package com.community.mingle.service.repository

import com.community.mingle.api.PostService
import com.community.mingle.service.models.CategoryResponse
import com.community.mingle.service.models.CommentList
import com.community.mingle.service.models.CommentSend
import com.community.mingle.service.models.Edit
import com.community.mingle.service.models.LikeResponse
import com.community.mingle.service.models.PostCommentResponse
import com.community.mingle.service.models.PostDetailResponse
import com.community.mingle.service.models.ReplySend
import com.community.mingle.service.models.ReportPost
import com.community.mingle.service.models.ReportResponse
import com.community.mingle.service.models.ResultResponse
import com.community.mingle.service.models.ScrapResponse
import com.community.mingle.service.models.WritePostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

/* 게시물 상세 Repository */

class PostRepository
@Inject
constructor(private val postService: PostService) {

    suspend fun getUnivPostDetail(univPostId: Int): Result<Response<PostDetailResponse>> = runCatching {
        postService.getUnivPostDetail(univPostId)
    }

    suspend fun getTotalPostDetail(totalPostId: Int): Result<Response<PostDetailResponse>> = runCatching {
        postService.getTotalPostDetail(totalPostId)
    }

    suspend fun editUnivPost(id: Int, edit: Edit): Result<Response<ResultResponse>> = runCatching {
        postService.editUnivPost(id, edit)
    }

    suspend fun editTotalPost(id: Int, edit: Edit): Result<Response<ResultResponse>> = runCatching {
        postService.editTotalPost(id, edit)
    }

    suspend fun deleteUnivPost(id: Int): Result<Response<ResultResponse>> = runCatching {
        postService.deleteUnivPost(id)
    }

    suspend fun deleteTotalPost(id: Int): Result<Response<ResultResponse>> = runCatching {
        postService.deleteTotalPost(id)
    }

    suspend fun likeUnivPost(postIdx: Int): Result<Response<LikeResponse>> = runCatching {
        postService.likeUnivPost(postIdx)
    }

    suspend fun unlikeUnivPost(postId: Int): Result<Response<ResultResponse>> = runCatching {
        postService.unlikeUnivPost(postId)
    }

    suspend fun likeTotalPost(postIdx: Int): Result<Response<LikeResponse>> = runCatching {
        postService.likeTotalPost(postIdx)
    }

    suspend fun unlikeTotalPost(postId: Int): Result<Response<ResultResponse>> = runCatching {
        postService.unlikeTotalPost(postId)
    }

    suspend fun scrapUniv(postIdx: Int): Result<Response<ScrapResponse>> = runCatching {
        postService.scrapUniv(postIdx)
    }

    suspend fun delscrapUniv(postId: Int): Result<Response<ResultResponse>> = runCatching {
        postService.delscrapUniv(postId)
    }

    suspend fun scrapTotal(postIdx: Int): Result<Response<ScrapResponse>> = runCatching {
        postService.scrapTotal(postIdx)
    }

    suspend fun delscrapTotal(postId: Int): Result<Response<ResultResponse>> = runCatching {
        postService.delscrapTotal(postId)
    }

    suspend fun createReport(post: ReportPost): Result<Response<ReportResponse>> = runCatching {
        postService.createReport(post)
    }

    suspend fun blindUnivPost(postId: Int): Result<Response<ResultResponse>> = runCatching {
        postService.blindUnivPost(postId)
    }

    suspend fun blindTotalPost(postId: Int): Result<Response<ResultResponse>> = runCatching {
        postService.blindTotalPost(postId)
    }

    suspend fun unblindUnivPost(postId: Int): Result<Response<ResultResponse>> = runCatching {
        postService.unblindUnivPost(postId)
    }

    suspend fun unblindTotalPost(postId: Int): Result<Response<ResultResponse>> = runCatching {
        postService.unblindTotalPost(postId)
    }

    suspend fun getUnivComment(univPostId: Int): Result<Response<CommentList>> = runCatching {
        postService.getUnivComment(univPostId)
    }

    suspend fun getTotalComment(totalPostId: Int): Result<Response<CommentList>> = runCatching {
        postService.getTotalComment(totalPostId)
    }

    suspend fun postUnivComment(comment: CommentSend): Result<Response<PostCommentResponse>> = runCatching {
        postService.postUnivComment(comment)
    }

    suspend fun postTotalComment(comment: CommentSend): Result<Response<PostCommentResponse>> = runCatching {
        postService.postTotalComment(comment)
    }

    suspend fun postUnivReply(reply: ReplySend): Result<Response<PostCommentResponse>> = runCatching {
        postService.postUnivReply(reply)
    }

    suspend fun postTotalReply(reply: ReplySend): Result<Response<PostCommentResponse>> = runCatching {
        postService.postTotalReply(reply)
}

    suspend fun deleteUnivComment(id: Int): Result<Response<ResultResponse>> = runCatching {
        postService.deleteUnivComment(id)
    }

    suspend fun deleteTotalComment(id: Int): Result<Response<ResultResponse>> = runCatching {
        postService.deleteTotalComment(id)
    }

    suspend fun commentLikeUniv(commentIdx: Int): Result<Response<LikeResponse>> = runCatching {
        postService.commentLikeUniv(commentIdx)
    }

    suspend fun commentUnlikeUniv(commentIdx: Int): Result<Response<ResultResponse>> = runCatching {
        postService.commentUnlikeUniv(commentIdx)
    }

    suspend fun commentLikeTotal(commentIdx: Int): Result<Response<LikeResponse>> = runCatching {
        postService.commentLikeTotal(commentIdx)
    }

    suspend fun commentUnlikeTotal(commentIdx: Int): Result<Response<ResultResponse>> = runCatching {
        postService.commentUnlikeTotal(commentIdx)
    }

    suspend fun writeUnivPost(
        categoryId: Int,
        title: RequestBody,
        content: RequestBody,
        anonymous: Boolean,
        multipartFile: ArrayList<MultipartBody.Part>?,
    ): Result<Response<WritePostResponse>> = runCatching {
        postService.writeUnivPost(categoryId, title, content, anonymous, multipartFile)
    }

    suspend fun writeTotalPost(
        categoryId: Int,
        title: RequestBody,
        content: RequestBody,
        anonymous: Boolean,
        multipartFile: ArrayList<MultipartBody.Part>?,
    ): Result<Response<WritePostResponse>> = runCatching {
        postService.writeTotalPost(categoryId, title, content, anonymous, multipartFile)
    }

    suspend fun getPostCategory(): Result<Response<CategoryResponse>> = runCatching {
        postService.getPostCategory()
    }



}
