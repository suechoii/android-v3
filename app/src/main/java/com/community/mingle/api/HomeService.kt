package com.community.mingle.api

import com.community.mingle.service.models.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface HomeService {

    /* 홈 화면 잔디밭 최신 게시글 */
    @GET("/home/univ/recent")
    suspend fun getUnivRecentPost(
    ): Response<HomeListResponse>

    /* 홈 화면 광장 최신 게시글 */
    @GET("/home/total/recent")
    suspend fun getTotalRecentPost(
    ): Response<HomeListResponse>

    /* 홈 화면 광장 베스트 게시물 */
    @GET("/home/total/best")
    suspend fun getTotalBestPost(
    ): Response<HomeListResponse>

    /* 홈 화면 배너 리스트 */
    @GET("/home/banner")
    suspend fun getBanner(
    ): Response<BannerResponse>

    /* 알림 가져오기 */
    @GET("member/notification")
    suspend fun getNotification(
    ): Response<NotificationResponse>

    /* 알림 정보 읽기 */
    @PATCH("member/notification")
    suspend fun readNotification(
        @Body notification : Notifications
    ): Response<ResultResponse>


    @GET("/home/unite/best")
    suspend fun getUniteBestList(
    ): HomeListResponse


}