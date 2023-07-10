package com.community.mingle.api

import com.community.mingle.service.models.PostListResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnivTotalService {
    /* 잔디밭 글 불러오기 */
    @GET("/post/univ")
    suspend fun getUnivPosts(
        @Query("category") category: Int, @Query("postId") postId: Int,
    ): Response<PostListResponse>
    /* 광장 글 불러오기 */
    @GET("/post/total")
    suspend fun getTotalPosts(
        @Query("category") category: Int, @Query("postId") postId: Int,
    ): Response<PostListResponse>
    /* 인기 잔디밭 글 불러오기 */
    @GET("/post/univ/best")
    suspend fun getUnivBestPosts(
        @Query("postId") postId: Int,
    ): Response<PostListResponse>
    /* 인기 광장 글 불러오기 */
    @GET("/post/total/best")
    suspend fun getTotalBestPosts(
        @Query("postId") postId: Int,
    ): Response<PostListResponse>
    /* 잔디밭 게시글 찾기 */
    @GET("/post/univ/search")
    suspend fun searchUnivPost(
        @Query("keyword") keyword: String,
    ): Response<PostListResponse>
    /* 광장 게시글 찾기 */
    @GET("/post/total/search")
    suspend fun searchTotalPost(
        @Query("keyword") keyword: String,
    ): Response<PostListResponse>

    @GET("/post/unite/best")
    suspend fun getUniteBest(
        @Query("totalPost") totalPost: Int,
        @Query("univPost") univPost: Int,
    ): PostListResponse

    @GET("/post/univ/posts")
    suspend fun getAllUnivPosts(
        @Query("postId") postId: Int,
    ): PostListResponse
}