package com.example.ethysell.model

data class ProductComment(
    val `data`: CommentData,
    val message: String,
    val success: Boolean
)

data class CommentData(
    val comment: String,
    val id: Int,
    val isHidden: Boolean,
    val itemId: Int,
    val user: UserComment
)

data class UserComment(
    val id: Int,
    val name: String,
    val role: String
)