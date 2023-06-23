package com.community.mingle.api

import com.community.mingle.service.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.net.URL

interface MarketService {

    /* 거래 게시판 글 작성 API */
    @Multipart
    @POST("/item")
    suspend fun createItemPost(@Part("title") title: RequestBody,
                               @Part("price") price: RequestBody,
                               @Part("content") content: RequestBody,
                               @Part("location") location: RequestBody,
                               @Part("chatUrl") chatUrl: RequestBody,
                               @Part("isAnonymous") anonymous: Boolean,
                               @Part multipartFile: ArrayList<MultipartBody.Part>?): Response<MarketWritePostResponse>

    /* 게시물 신고 */
    @POST("/member/report")
    suspend fun createReport(
        @Body post: ReportPost
    )  : Response<ReportResponse>

    /* 거래 게시물 찜 */
    @POST("/item/like")
    suspend fun createItemLike(
        @Query("itemId") itemId: Int
    ): Response<ResultResponse>

    /* 거래 댓글 작성 */
    @POST("/item/comment")
    suspend fun commentPost(
       @Body comment: MarketCommentSend
    ): Response<PostCommentResponse>

    /* 거래 대댓글 작성 */
    @POST("/item/comment")
    suspend fun replyPost(
        @Body comment: MarketReplySend
    ) : Response <PostCommentResponse>

    /* 거래 게시판 글 상세 API */
    @GET("/item/{itemId}")
    suspend fun getItemPostDetail(
        @Path("itemId") itemId: Int
    ) : Response<MarketItemDetailResponse>

    /* 거래 게시물 수정 API*/
    @Multipart
    @PATCH("/item/{itemId}")
    suspend fun modifyItemPost(
        @Path("itemId") itemId: Int,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("price") price: RequestBody,
        @Part("location") location: RequestBody,
        @Part("chatUrl") chatUrl: RequestBody,
        @Part("itemImageUrlsToDelete") itemImageUrlsToDelete: RequestBody,
        @Part itemImagesToAdd: ArrayList<MultipartBody.Part>?
    ) : Response<ResultResponse>

    /* 거래 게시물 삭제*/
    @PATCH("/item/status/{itemId}")
    suspend fun deleteItemPost(
        @Path("itemId") itemId: Int
    ) : Response<ResultResponse>

    /* 판매 상태 변경*/
    @PATCH("/item/item-status/{itemId}")
    suspend fun modifyItemStatus(
        @Path("itemId") itemId: Int, @Query("itemStatus") itemStatus: String
    ) : Response<ResultResponse>

    /* 거래 댓글 삭제 */
    @PATCH("/item/comment/{itemCommentId}")
    suspend fun commentDelete(
        @Path("itemCommentId") itemId: Int
    ) : Response<ResultResponse>

    /* 거래 게시판 리스트 조회 API */
    @GET("/item/list")
    suspend fun getItemList(
        @Query("itemId") itemId: Int
    ) : Response<MarketListResponse>

    /* 거래 댓글 조회 */
    @GET("/item/comment/{itemId}")
    suspend fun getComment(
        @Path("itemId") itemId: Int
    ) : Response<CommentList>

    /* 거래 게시물 찜 취소 */
    @DELETE("/item/unlike")
    suspend fun itemUnlike(
        @Query("itemId") itemId: Int
    ) : Response<ResultResponse>

    /* 중고거레 게시판 검색 */
    @GET("/item/search")
    suspend fun searchMarket(
        @Query("keyword") keyword: String
    ) : Response<MarketListResponse>

    /* 내가 찜한 거래 게시물 조회*/
    @GET("/member/items/like")
    suspend fun getLikedItems(
        @Query("itemId") itemId: Int
    ) : Response<MarketListResponse>

    /* 내가 쓴 거래 게시물 조회*/
    @GET("/member/items")
    suspend fun getMyItemList(
        @Query("itemId") itemId: Int,
        @Query("itemStatus") itemStatus: String
    ) : Response<MarketListResponse>

}