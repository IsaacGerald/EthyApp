package com.example.ethysell.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemDelete(
    val message: String,
    val success: Boolean
): Parcelable