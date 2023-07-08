package com.community.mingle.model.post

data class HomeHotPost(
    val postId: Int,
    val postType: PostType?,
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
