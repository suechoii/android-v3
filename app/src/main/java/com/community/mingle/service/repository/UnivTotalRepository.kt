package com.community.mingle.service.repository

import com.community.mingle.api.UnivTotalService
import javax.inject.Inject


class UnivTotalRepository
@Inject
constructor(private val univTotalService: UnivTotalService) {
    suspend fun getUnivPost(category: Int, postId: Int) =
        univTotalService.getUnivPosts(category, postId)

    suspend fun getTotalPost(category: Int, postId: Int) =
        univTotalService.getTotalPosts(category, postId)

    suspend fun getUnivBestPost(postId: Int)=
        univTotalService.getUnivBestPosts(postId)

    suspend fun getTotalBestPost(postId: Int)=
        univTotalService.getTotalBestPosts(postId)

    suspend fun searchUnivPost(keyword: String)=
        univTotalService.searchUnivPost(keyword)

    suspend fun searchTotalPost(keyword: String)=
        univTotalService.searchTotalPost(keyword)

}
