package com.community.mingle.model.post

data class HotPostBoardListItem(
    val postType: PostType,
    val postId: Int,
    val title: String,
    val contents: String,
    val nickname: String,
    val likeCount: String,
    val commentCount: String,
    val createdAt: String,
    val blinded: Boolean,
    val fileAttached: Boolean,
    val reported: Boolean,
)
