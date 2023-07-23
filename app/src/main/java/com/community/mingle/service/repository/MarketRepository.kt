package com.community.mingle.service.repository

import com.community.mingle.api.MarketService
import com.community.mingle.service.models.CommentList
import com.community.mingle.service.models.market.MarketCommentSend
import com.community.mingle.service.models.market.MarketItemDetailResponse
import com.community.mingle.service.models.market.MarketListResponse
import com.community.mingle.service.models.market.MarketReplySend
import com.community.mingle.service.models.market.MarketWritePostResponse
import com.community.mingle.service.models.PostCommentResponse
import com.community.mingle.service.models.ReportPost
import com.community.mingle.service.models.ReportResponse
import com.community.mingle.service.models.ResultResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

/* 게시물 상세 Repository */

class MarketRepository
@Inject
constructor(private val marketService: MarketService) {

    suspend fun createItemPost(
        title: RequestBody, price: RequestBody, content: RequestBody, location: RequestBody, chatUrl: RequestBody,
        isAnonymous: Boolean, multipartFile: ArrayList<MultipartBody.Part>?,
    ):Result<Response<MarketWritePostResponse>> = runCatching {
        marketService.createItemPost(title, price, content, location, chatUrl, isAnonymous, multipartFile)
    }

    suspend fun createItemLike(itemId: Int) :Result<Response<ResultResponse>> = runCatching {
        marketService.createItemLike(itemId)
    }

    suspend fun createReport(post: ReportPost):Result<Response<ReportResponse>> = runCatching {
        marketService.createReport(post)
    }

    suspend fun commentPost(comment: MarketCommentSend):Result<Response<PostCommentResponse>> = runCatching {
        marketService.commentPost(comment)
    }

    suspend fun replyPost(reply: MarketReplySend): Result<Response<PostCommentResponse>> = runCatching {
        marketService.replyPost(reply)
    }

    suspend fun getItemPostDetail(itemId: Int): Result<Response<MarketItemDetailResponse>> = runCatching {
        marketService.getItemPostDetail(itemId)
    }

    suspend fun modifyItemPost(
        itemId: Int,
        title: RequestBody,
        content: RequestBody,
        price: RequestBody,
        location: RequestBody,
        chatUrl: RequestBody,
        itemImageUrlsToDelete: RequestBody,
        isAnonymous: RequestBody,
        itemImagesToAdd: ArrayList<MultipartBody.Part>?,
    ):Result<Response<ResultResponse>> = runCatching {
        marketService.modifyItemPost(itemId, title, content, price, location, chatUrl, itemImageUrlsToDelete,isAnonymous, itemImagesToAdd)
    }
    suspend fun deleteItemPost(itemId: Int):Result<Response<ResultResponse>> = runCatching {
        marketService.deleteItemPost(itemId)
    }

    suspend fun modifyItemStatus(itemId: Int, itemStatus: String):Result<Response<ResultResponse>> = runCatching {
        marketService.modifyItemStatus(itemId, itemStatus)
    }

    suspend fun commentDelete(itemCommentId: Int):Result<Response<ResultResponse>> = runCatching {
        marketService.commentDelete(itemCommentId)
    }

    suspend fun getItemList(itemId: Int) :Result<Response<MarketListResponse>> = runCatching {
        marketService.getItemList(itemId)
    }

    suspend fun getComment(itemId: Int): Result<Response<CommentList>> = runCatching {
        marketService.getComment(itemId)
    }

    suspend fun itemUnlike(itemId: Int): Result<Response<ResultResponse>> = runCatching {
        marketService.itemUnlike(itemId)
    }

    suspend fun itemSearch(keyword: String): Result<Response<MarketListResponse>> = runCatching {
        marketService.searchMarket(keyword)
    }

    suspend fun myLikedItems(itemId: Int): Result<Response<MarketListResponse>> = runCatching {
        marketService.getLikedItems(itemId)
    }

    suspend fun myItemList(itemId: Int, itemStatus: String): Result<Response<MarketListResponse>> = runCatching {
        marketService.getMyItemList(itemId, itemStatus)
    }

    suspend fun getMarketCurrencies() : Result<List<String>> = runCatching {
        val response = marketService.getMarketCurrencies()
        if(!response.isSuccess) throw Exception(response.message)
        return@runCatching response.result
    }
}
