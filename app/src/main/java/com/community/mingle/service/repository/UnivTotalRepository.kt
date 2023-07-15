package com.community.mingle.service.repository

import com.community.mingle.api.UnivTotalService
import com.community.mingle.model.post.PostType
import com.community.mingle.service.models.PostListResponse
import com.community.mingle.service.models.PostResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class UnivTotalRepository
@Inject
constructor(
    private val univTotalService: UnivTotalService,
) {

    suspend fun getUnivPost(category: Int, postId: Int): Result<Response<PostListResponse>> = runCatching {
        univTotalService.getUnivPosts(category, postId)
    }

    suspend fun getTotalPost(category: Int, postId: Int): Result<Response<PostListResponse>> = runCatching {
        univTotalService.getTotalPosts(category, postId)
    }

    suspend fun getUnivBestPost(postId: Int): Result<Response<PostListResponse>> = runCatching {
        univTotalService.getUnivBestPosts(postId)
    }

    suspend fun getTotalBestPost(postId: Int): Result<Response<PostListResponse>> = runCatching {
        univTotalService.getTotalBestPosts(postId)
    }

    suspend fun searchUnivPost(keyword: String): Result<Response<PostListResponse>> = runCatching {
        univTotalService.searchUnivPost(keyword)
    }

    suspend fun searchTotalPost(keyword: String): Result<Response<PostListResponse>> = runCatching {
        univTotalService.searchTotalPost(keyword)
    }

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
        }
    }

    suspend fun getAllUnivPostList(lastPostId: Int) : Result<List<PostResult>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = univTotalService.getAllUnivPosts(lastPostId)
            if(response.code == 3031) return@runCatching emptyList()
            if (response.code != 1000) throw IllegalStateException(response.message)
            response.result.postListDTO
        }
    }

    suspend fun getAllTotalPostList(lastPostId: Int) : Result<List<PostResult>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = univTotalService.getAllTotalPosts(lastPostId)
            if(response.code == 3031) return@runCatching emptyList()
            if (response.code != 1000) throw IllegalStateException(response.message)
            response.result.postListDTO
        }
    }
}
