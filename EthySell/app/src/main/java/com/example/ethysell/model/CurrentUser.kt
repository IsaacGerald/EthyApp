package com.example.ethysell.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class CurrentUser(
    val `data`: UserData,
    val message: String,
    val success: Boolean
) : Parcelable

@Parcelize
data class UserData(
    val token: String,
    val userInfo: UserInfo
) : Parcelable

@Parcelize
data class UserInfo(
    val api_token: String,
    val created_at: String,
    val email: String,
    val email_verified_at: @RawValue Any,
    val id: Int,
    val name: String,
    val role: String,
    val updated_at: String
) : Parcelable

data class Register(val success: Boolean, val message: String)