package com.example.ethysell.model

import android.os.Message
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize
@JsonClass(generateAdapter = true)
@Parcelize
data class Product(
    val success: Boolean,
    val message: String,
    val `data`: List<Data>,

):Parcelable

@Parcelize
data class Data(
    val barcodeNo: String,
    var comments: List<Comment>?,
    val description: String?,
    val ethicalScore: String,
    val id: Int,
    val imageUrl: String?,
    val name: String,
    val type: String
): Parcelable


@Parcelize
data class Comment(
    val comment: String,
    val id: Int,
    val isHidden: Boolean,
    val itemId: String,
    val user: User
): Parcelable

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val role: String
) : Parcelable