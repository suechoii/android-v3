package com.community.mingle.service.repository

import com.community.mingle.api.HomeService
import com.community.mingle.service.models.Notifications
import javax.inject.Inject


class HomeRepository
@Inject
constructor(private val homeService: HomeService) {
    suspend fun getUnivRecentPost() =
        homeService.getUnivRecentPost()

    suspend fun getTotalRecentPost() =
        homeService.getTotalRecentPost()

    suspend fun getUnivBestPost() =
        homeService.getUnivBestPost()

    suspend fun getTotalBestPost() =
        homeService.getTotalBestPost()

    suspend fun getBanner() =
        homeService.getBanner()

    suspend fun getNotification() =
        homeService.getNotification()

    suspend fun readNotification(notifications: Notifications) =
        homeService.readNotification(notifications)

}
