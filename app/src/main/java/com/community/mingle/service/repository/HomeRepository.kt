package com.community.mingle.service.repository

import com.community.mingle.api.HomeService
import com.community.mingle.model.post.HomeHotPost
import com.community.mingle.service.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import kotlin.Result

class HomeRepository
@Inject
constructor(private val homeService: HomeService) {
    suspend fun getUnivRecentPost():Result<Response<HomeListResponse>> = runCatching {
        homeService.getUnivRecentPost()
    }

    suspend fun getTotalRecentPost():Result<Response<HomeListResponse>> = runCatching {
        homeService.getTotalRecentPost()
    }

    suspend fun getBanner(): Result<Response<BannerResponse>> = runCatching {
        homeService.getBanner()
    }

    suspend fun getNotification():Result<Response<NotificationResponse>> = runCatching {
        homeService.getNotification()
    }

    suspend fun readNotification(notifications: Notifications):Result<Response<ResultResponse>> = runCatching {
        homeService.readNotification(notifications)
    }

    suspend fun getUniteBestList(): Flow<List<HomeResult>> = flow {
        val response = homeService.getUniteBestList()
        if(response.code == 1000) {
            emit(response.result)
        }
        else {
            throw IllegalStateException(response.message)
        }
    }.flowOn(Dispatchers.IO)

}
