package com.community.mingle.api

import com.community.mingle.service.models.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface PostService {

    /* 해당 id의 잔디밭 게시물 상세 불러오기 */
    @GET("/post/univ/{univPostId}")
    suspend fun getUnivPostDetail(
        @Path("univPostId") univPostId: Int
    ): Response<PostDetailResponse>

    /* 해당 id의 광장 게시물 상세 불러오기 */
    @GET("/post/total/{totalPostId}")
    suspend fun getTotalPostDetail(
        @Path("totalPostId") totalPostId: Int
    ): Response<PostDetailResponse>

    /* 잔디밭 게시물 수정 */
    @PATCH("/post/univ/{id}")
    suspend fun editUnivPost(
        @Path ("id") id: Int, @Body edit: Edit
    ): Response<ResultResponse>

    /* 광장 게시물 수정 */
    @PATCH("/post/total/{id}")
    suspend fun editTotalPost(
        @Path ("id") id: Int, @Body edit: Edit
    ): Response<ResultResponse>

    /* 잔디밭 게시물 삭제 */
    @PATCH("/post/univ/status/{id}")
    suspend fun deleteUnivPost(
        @Path("id") id: Int
    ) : Response<ResultResponse>

    /* 광장 게시물 삭제 */
    @PATCH("/post/total/status/{id}")
    suspend fun deleteTotalPost(
        @Path("id") id: Int
    ) : Response<ResultResponse>

    /* 잔디밭 게시물 좋아요 */
    @POST("/post/univ/likes")
    suspend fun likeUnivPost(
        @Query("postIdx") postIdx: Int
    ) : Response<LikeResponse>

    /* 잔디밭 게시물 좋아요 취소 */
    @DELETE("/post/univ/unlike")
    suspend fun unlikeUnivPost(
        @Query("postId") postId: Int
    ) : Response<ResultResponse>

    /* 광장 게시물 좋아요 */
    @POST("/post/total/likes")
    suspend fun likeTotalPost(
        @Query("postIdx") postIdx: Int
    ) : Response<LikeResponse>

    /* 광장 게시물 좋아요 취소 */
    @DELETE("/post/total/unlike")
    suspend fun unlikeTotalPost(
        @Query("postId") postId: Int
    ) : Response<ResultResponse>

    /* 잔디밭 게시물 스크랩 */
    @POST("/post/univ/scrap")
    suspend fun scrapUniv(
        @Query("postIdx") postIdx: Int
    ) : Response<ScrapResponse>

    /* 잔디밭 게시물 스크랩 취소 */
    @DELETE("/post/univ/deletescrap")
    suspend fun delscrapUniv(
        @Query("postId") postId: Int
    ) : Response<ResultResponse>

    /* 광장 게시물 스크랩 */
    @POST("/post/total/scrap")
    suspend fun scrapTotal(
        @Query("postIdx") postIdx: Int
    ) : Response<ScrapResponse>

    /* 광장 게시물 스크랩 취소 */
    @DELETE("/post/total/deletescrap")
    suspend fun delscrapTotal(
        @Query("postId") postId: Int
    ) : Response<ResultResponse>

    /* 게시물 및 댓글 신고 */
    @POST("/member/report")
    suspend fun createReport(
        @Body post: ReportPost
    )  : Response<ReportResponse>

    /* 잔디밭 게시글 가리기 */
    @POST("/post/univ/blind")
    suspend fun blindUnivPost(
        @Query("postId") postId : Int
    ) : Response<ResultResponse>

    /* 광장 게시글 가리기 */
    @POST("/post/total/blind")
    suspend fun blindTotalPost(
        @Query("postId") postId : Int
    ) : Response<ResultResponse>

    /* 잔디밭 게시글 가리기 취소 */
    @DELETE("/post/univ/deleteblind")
    suspend fun unblindUnivPost(
        @Query("postId") postId : Int
    ) : Response<ResultResponse>

    /* 광장 게시글 가리기 취소 */
    @DELETE("/post/total/deleteblind")
    suspend fun unblindTotalPost(
        @Query("postId") postId : Int
    ) : Response<ResultResponse>

    /* 잔디밭 게시물 댓글 조회 */
    @GET("/post/univ/{univPostId}/comment")
    suspend fun getUnivComment(
        @Path("univPostId") univPostId: Int
    ): Response<CommentList>

    /* 광장 게시물 댓글 조회 */
    @GET("/post/total/{totalPostId}/comment")
    suspend fun getTotalComment(
        @Path("totalPostId") totalPostId: Int
    ): Response<CommentList>

    /* 잔디밭 게시물 댓글 작성 */
    @POST("/comment/univ")
    suspend fun postUnivComment(
        @Body comment: CommentSend
    ) : Response <PostCommentResponse>

    /* 광장 게시물 댓글 작성 */
    @POST("/comment/total")
    suspend fun postTotalComment(
        @Body comment: CommentSend
    ) : Response <PostCommentResponse>

    /* 잔디밭 게시물 댓글 작성 */
    @POST("/comment/univ")
    suspend fun postUnivReply(
        @Body comment: ReplySend
    ) : Response <PostCommentResponse>

    /* 광장 게시물 댓글 작성 */
    @POST("/comment/total")
    suspend fun postTotalReply(
        @Body comment: ReplySend
    ) : Response <PostCommentResponse>

    /* 학교 게시물 댓글 삭제 */
    @PATCH("/comment/univ/status/{id}")
    suspend fun deleteUnivComment(
        @Path("id") id: Int
    ) : Response<ResultResponse>

    /* 통합 게시물 댓글 삭제 */
    @PATCH("/comment/total/status/{id}")
    suspend fun deleteTotalComment(
        @Path("id") id: Int
    ) : Response<ResultResponse>

    /* 잔디밭 게시물 댓글 좋아요 */
    @POST("/comment/univ/likes")
    suspend fun commentLikeUniv(
        @Query("commentIdx") commentIdx: Int
    ) : Response<LikeResponse>

    /* 잔디밭 게시물 댓글 좋아요 취소 */
    @DELETE("/comment/univ/unlike")
    suspend fun commentUnlikeUniv(
        @Query("commentIdx") commentIdx: Int
    ) : Response<ResultResponse>

    /* 광장 게시물 댓글 좋아요 */
    @POST("/comment/total/likes")
    suspend fun commentLikeTotal(
        @Query("commentIdx") commentIdx: Int
    ) : Response<LikeResponse>

    /* 광장 게시물 댓글 좋아요 취소 */
    @DELETE("/comment/total/unlike")
    suspend fun commentUnlikeTotal(
        @Query("commentIdx") commentIdx: Int
    ) : Response<ResultResponse>

    /* 잔디밭 글 작성 */
    @Multipart
    @POST("/post/univ")
    suspend fun writeUnivPost(@Part("categoryId") categoryId: Int,
                     @Part("title") title: RequestBody,
                     @Part("content") content: RequestBody,
                     @Part("isAnonymous") anonymous: Boolean,
                     @Part multipartFile: ArrayList<MultipartBody.Part>?): Response<WritePostResponse>

    /* 광장 글 작성 */
    @Multipart
    @POST("/post/total")
    suspend fun writeTotalPost(@Part("categoryId") categoryId: Int,
                      @Part("title") title: RequestBody,
                      @Part("content") content: RequestBody,
                      @Part("isAnonymous") anonymous: Boolean,
                      @Part multipartFile: ArrayList<MultipartBody.Part>?): Response<WritePostResponse>

    /* 게시물 카테고리 불러오기 */
    @GET("/post/category")
    suspend fun getPostCategory(
    ) : Response<CategoryResponse>

    @GET("/post/unite/best")
    suspend fun getUniteBest(
        @Query("totalPost") totalPost: Int,
        @Query("univPost") univPost: Int,
    ): HomeListResponse

}