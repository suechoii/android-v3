package com.community.mingle.service.repository

import com.community.mingle.api.MarketService
import com.community.mingle.service.models.*
import com.community.mingle.service.models.market.MarketCommentSend
import com.community.mingle.service.models.market.MarketItemDetailResponse
import com.community.mingle.service.models.market.MarketListResponse
import com.community.mingle.service.models.market.MarketReplySend
import com.community.mingle.service.models.market.MarketWritePostResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import kotlin.Result

/* 게시물 상세 Repository */

class MarketRepository
@Inject
constructor(private val marketService: MarketService) {

    suspend fun createItemPost(
        title: RequestBody,
        price: RequestBody,
        content: RequestBody,
        location: RequestBody,
        chatUrl: RequestBody,
        isAnonymous: Boolean,
        multipartFile: ArrayList<MultipartBody.Part>?,
        currency: String,
    ): Result<Response<MarketWritePostResponse>> = runCatching {
        val currencyRequestBody = currency.toRequestBody("text/plain".toMediaTypeOrNull())
        marketService.createItemPost(
            title = title,
            price = price,
            content = content,
            location = location,
            chatUrl = chatUrl,
            currency = currencyRequestBody,
            anonymous = isAnonymous,
            multipartFile = multipartFile
        )
    }

    suspend fun createItemLike(itemId: Int): Result<Response<ResultResponse>> = runCatching {
        marketService.createItemLike(itemId)
    }

    suspend fun createReport(post: ReportPost): Result<Response<ReportResponse>> = runCatching {
        marketService.createReport(post)
    }

    suspend fun commentPost(comment: MarketCommentSend): Result<Response<PostCommentResponse>> = runCatching {
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
        currency: String,
    ): Result<Response<ResultResponse>> = runCatching {
        val currencyRequestBody = currency.toRequestBody("text/plain".toMediaTypeOrNull())
        marketService.modifyItemPost(
            itemId = itemId,
            title = title,
            content = content,
            price = price,
            location = location,
            chatUrl = chatUrl,
            itemImageUrlsToDelete = itemImageUrlsToDelete,
            anonymous = isAnonymous,
            currency = currencyRequestBody,
            itemImagesToAdd = itemImagesToAdd,
        )
    }

    suspend fun deleteItemPost(itemId: Int): Result<Response<ResultResponse>> = runCatching {
        marketService.deleteItemPost(itemId)
    }

    suspend fun modifyItemStatus(itemId: Int, itemStatus: String): Result<Response<ResultResponse>> = runCatching {
        marketService.modifyItemStatus(itemId, itemStatus)
    }

    suspend fun commentDelete(itemCommentId: Int): Result<Response<ResultResponse>> = runCatching {
        marketService.commentDelete(itemCommentId)
    }

    suspend fun getItemList(itemId: Int): Result<Response<MarketListResponse>> = runCatching {
        marketService.getItemList(itemId)
    }

    suspend fun getComment(itemId: Int): Result<Response<CommentList>> = runCatching {
        marketService.getComment(itemId)
    }

    suspend fun itemUnlike(itemId: Int): Result<Response<ResultResponse>> = runCatching {
        marketService.itemUnlike(itemId)
    }

    suspend fun commentLikeMarket(itemId: Int) : Result<Response<LikeResponse>> = runCatching {
        marketService.likeItemComment(itemId)
    }

    suspend fun commentUnlikeMarket(itemId: Int) : Result<Response<ResultResponse>> = runCatching {
        marketService.unlikeItemComment(itemId)
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

    suspend fun getMarketCurrencies(): Result<List<String>> = runCatching {
        val response = marketService.getMarketCurrencies()
        if (!response.isSuccess) throw Exception(response.message)
        return@runCatching response.result
    }
}
