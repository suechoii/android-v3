package com.community.mingle.service.repository

import com.community.mingle.api.PostService
import com.community.mingle.model.post.HomeHotPost
import com.community.mingle.service.models.CommentSend
import com.community.mingle.service.models.Edit
import com.community.mingle.service.models.ReplySend
import com.community.mingle.service.models.ReportPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

/* 게시물 상세 Repository */

class PostRepository
@Inject
constructor(private val postService: PostService) {

    suspend fun getUnivPostDetail(univPostId: Int) =
        postService.getUnivPostDetail(univPostId)

    suspend fun getTotalPostDetail(totalPostId: Int) =
        postService.getTotalPostDetail(totalPostId)

    suspend fun editUnivPost(id: Int, edit: Edit) =
        postService.editUnivPost(id, edit)

    suspend fun editTotalPost(id: Int, edit: Edit) =
        postService.editTotalPost(id, edit)

    suspend fun deleteUnivPost(id: Int) =
        postService.deleteUnivPost(id)

    suspend fun deleteTotalPost(id: Int) =
        postService.deleteTotalPost(id)

    suspend fun likeUnivPost(postIdx: Int) =
        postService.likeUnivPost(postIdx)

    suspend fun unlikeUnivPost(postId: Int) =
        postService.unlikeUnivPost(postId)

    suspend fun likeTotalPost(postIdx: Int) =
        postService.likeTotalPost(postIdx)

    suspend fun unlikeTotalPost(postId: Int) =
        postService.unlikeTotalPost(postId)

    suspend fun scrapUniv(postIdx: Int) =
        postService.scrapUniv(postIdx)

    suspend fun delscrapUniv(postId: Int) =
        postService.delscrapUniv(postId)

    suspend fun scrapTotal(postIdx: Int) =
        postService.scrapTotal(postIdx)

    suspend fun delscrapTotal(postId: Int) =
        postService.delscrapTotal(postId)

    suspend fun createReport(post: ReportPost) =
        postService.createReport(post)

    suspend fun blindUnivPost(postId: Int) =
        postService.blindUnivPost(postId)

    suspend fun blindTotalPost(postId: Int) =
        postService.blindTotalPost(postId)

    suspend fun unblindUnivPost(postId: Int) =
        postService.unblindUnivPost(postId)

    suspend fun unblindTotalPost(postId: Int) =
        postService.unblindTotalPost(postId)

    suspend fun getUnivComment(univPostId: Int) =
        postService.getUnivComment(univPostId)

    suspend fun getTotalComment(totalPostId: Int) =
        postService.getTotalComment(totalPostId)

    suspend fun postUnivComment(comment: CommentSend) =
        postService.postUnivComment(comment)

    suspend fun postTotalComment(comment: CommentSend) =
        postService.postTotalComment(comment)

    suspend fun postUnivReply(reply: ReplySend) =
        postService.postUnivReply(reply)

    suspend fun postTotalReply(reply: ReplySend) =
        postService.postTotalReply(reply)

    suspend fun deleteUnivComment(id: Int) =
        postService.deleteUnivComment(id)

    suspend fun deleteTotalComment(id: Int) =
        postService.deleteTotalComment(id)

    suspend fun commentLikeUniv(commentIdx: Int) =
        postService.commentLikeUniv(commentIdx)

    suspend fun commentUnlikeUniv(commentIdx: Int) =
        postService.commentUnlikeUniv(commentIdx)

    suspend fun commentLikeTotal(commentIdx: Int) =
        postService.commentLikeTotal(commentIdx)

    suspend fun commentUnlikeTotal(commentIdx: Int) =
        postService.commentUnlikeTotal(commentIdx)

    suspend fun writeUnivPost(
        categoryId: Int,
        title: RequestBody,
        content: RequestBody,
        anonymous: Boolean,
        multipartFile: ArrayList<MultipartBody.Part>?,
    ) =
        postService.writeUnivPost(categoryId, title, content, anonymous, multipartFile)

    suspend fun writeTotalPost(
        categoryId: Int,
        title: RequestBody,
        content: RequestBody,
        anonymous: Boolean,
        multipartFile: ArrayList<MultipartBody.Part>?,
    ) =
        postService.writeTotalPost(categoryId, title, content, anonymous, multipartFile)

    suspend fun getPostCategory() =
        postService.getPostCategory()

    suspend fun getUniteBestPostList(
        lastTotalPost: Int,
        lastUnivPost: Int,
    ): Flow<List<HomeHotPost>> = flow {
        val response = postService.getUniteBest(
            totalPost = lastTotalPost,
            univPost = lastUnivPost,
        )
        if (response.code == 1000) {
            emit(response.result.map { it.toHotPost() })
        } else {
            throw IllegalStateException(response.message)
        }
    }.flowOn(Dispatchers.IO)

}
