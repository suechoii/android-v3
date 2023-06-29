package com.community.mingle.paging

import androidx.paging.PagingSource
import com.community.mingle.api.PostService

class PostPagingDataSource(
    val postService: PostService,
    val query: String,
) : PagingSource<Pair<Int,Int>, HotPost>(){
}