package com.community.mingle.service.repository

import com.community.mingle.api.HomeService
import com.community.mingle.model.HotPost
import com.community.mingle.service.models.Notifications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepository
@Inject
constructor(private val homeService: HomeService) {
    suspend fun getUnivRecentPost() =
        homeService.getUnivRecentPost()

    suspend fun getTotalRecentPost() =
        homeService.getTotalRecentPost()

    suspend fun getBanner() =
        homeService.getBanner()

    suspend fun getNotification() =
        homeService.getNotification()

    suspend fun readNotification(notifications: Notifications) =
        homeService.readNotification(notifications)

    suspend fun getUniteBestList(): Flow<List<HotPost>> = flow {
        val response = homeService.getUniteBestList()
        if(response.code == 1000) {
            emit(response.result.map { it.toHotPost() })
        }
        else {
            throw IllegalStateException(response.message)
        }
    }.flowOn(Dispatchers.IO)

}
