package com.community.mingle.model.post

import com.community.mingle.model.post.PostType

data class HomeHotPost(
    val postId: Int,
    val postType: PostType = PostType.Total, // TODO: change by type
    val title: String,
    val contents: String,
    val nickname: String,
    val likeCount: String,
    val commentCount: String,
    val createdAt: String,
    var blinded: Boolean,
    val fileAttached: Boolean,
    val reported: Boolean,
)
