package com.example.ethysell.network

import com.example.ethysell.model.Data

data class PostResponse(
    val `data`: Data,
    val message: String,
    val success: Boolean
)


