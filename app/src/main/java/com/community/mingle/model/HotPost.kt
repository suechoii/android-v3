package com.community.mingle.model

data class HotPost(
    val postId: Int,
    val title: String,
    val contents: String,
    val nickname: String,
    val likeCount: Int,
    val commentCount: Int,
    val createdAt: String,
    var blinded: Boolean,
    val fileAttached: Boolean,
    val reported: Boolean,
)
