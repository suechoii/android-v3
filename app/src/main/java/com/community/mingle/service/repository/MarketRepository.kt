package com.community.mingle.service.repository

import com.community.mingle.api.MarketService
import com.community.mingle.api.PostService
import com.community.mingle.service.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.net.URL
import javax.inject.Inject

/* 게시물 상세 Repository */

class MarketRepository
@Inject
constructor(private val marketService: MarketService) {

    suspend fun createItemPost(title: RequestBody, price: RequestBody, content: RequestBody, location: RequestBody, chatUrl: RequestBody, isAnonymous: Boolean, multipartFile: ArrayList<MultipartBody.Part>?) =
        marketService.createItemPost(title, price, content, location, chatUrl, isAnonymous, multipartFile)

    suspend fun createItemLike(itemId: Int) =
        marketService.createItemLike(itemId)

    suspend fun createReport(post: ReportPost) =
        marketService.createReport(post)

    suspend fun commentPost(comment: MarketCommentSend) =
        marketService.commentPost(comment)

    suspend fun replyPost(reply: MarketReplySend) =
        marketService.replyPost(reply)

    suspend fun getItemPostDetail(itemId: Int) =
        marketService.getItemPostDetail(itemId)

    suspend fun modifyItemPost(itemId: Int, title: RequestBody, content: RequestBody, price: RequestBody, location: RequestBody, chatUrl: RequestBody, itemImageUrlsToDelete: RequestBody, itemImagesToAdd:ArrayList<MultipartBody.Part>? ) =
        marketService.modifyItemPost(itemId, title, content, price, location, chatUrl, itemImageUrlsToDelete, itemImagesToAdd)

    suspend fun deleteItemPost(itemId: Int) =
        marketService.deleteItemPost(itemId)

    suspend fun modifyItemStatus(itemId: Int, itemStatus: String) =
        marketService.modifyItemStatus(itemId, itemStatus)

    suspend fun commentDelete(itemCommentId: Int) =
        marketService.commentDelete(itemCommentId)

    suspend fun getItemList(itemId: Int) =
        marketService.getItemList(itemId)

    suspend fun getComment(itemId: Int) =
        marketService.getComment(itemId)

    suspend fun itemUnlike(itemId: Int) =
        marketService.itemUnlike(itemId)

    suspend fun itemSearch(keyword: String) =
        marketService.searchMarket(keyword)

    suspend fun myLikedItems(itemId: Int) =
        marketService.getLikedItems(itemId)

    suspend fun myItemList(itemId: Int, itemStatus: String) =
        marketService.getMyItemList(itemId, itemStatus)


}
