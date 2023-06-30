package com.community.mingle.service.repository

import com.community.mingle.api.UnivTotalService
import com.community.mingle.model.post.PostType
import com.community.mingle.service.models.PostResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UnivTotalRepository
@Inject
constructor(
    private val univTotalService: UnivTotalService,
) {

    suspend fun getUnivPost(category: Int, postId: Int) =
        univTotalService.getUnivPosts(category, postId)

    suspend fun getTotalPost(category: Int, postId: Int) =
        univTotalService.getTotalPosts(category, postId)

    suspend fun getUnivBestPost(postId: Int) =
        univTotalService.getUnivBestPosts(postId)

    suspend fun getTotalBestPost(postId: Int) =
        univTotalService.getTotalBestPosts(postId)

    suspend fun searchUnivPost(keyword: String) =
        univTotalService.searchUnivPost(keyword)

    suspend fun searchTotalPost(keyword: String) =
        univTotalService.searchTotalPost(keyword)

    suspend fun getHotPostList(
        lastUnivBestPostId: Int,
        lastTotalBestPostId: Int,
    ): Result<List<PostResult>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = univTotalService.getUniteBest(
                univPost = lastUnivBestPostId,
                totalPost = lastTotalBestPostId,
            )
            if (response.code != 1000) throw IllegalStateException(response.message)
            response.result.postListDTO
                .onEach { it.boardType = if (response.result.boardName == null) PostType.Total else PostType.Univ } // TODO: need new model class
        }
    }
}
