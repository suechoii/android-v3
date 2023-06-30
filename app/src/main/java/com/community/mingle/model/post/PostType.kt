package com.community.mingle.model.post

sealed interface PostType {
    object Total : PostType
    object Univ: PostType
}
