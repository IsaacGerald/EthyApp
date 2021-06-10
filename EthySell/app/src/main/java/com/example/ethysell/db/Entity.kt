package com.example.ethysell.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.ethysell.model.Comment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
@Entity(tableName = "data_table")
data class DataBaseData(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val barcodeNo: String,
    val comments: List<Comment>?,
    val description: String?,
    val ethicalScore: String,
    val imageUrl: String?,
    val name: String,
    val type: String
): Parcelable

class  CommentTypeConvertors{
    @TypeConverter
    fun fromList(list: List<Comment>?): String{
        return Gson().toJson(list)

    }

    @TypeConverter
    fun fromString(value: String): List<Comment>{
        val listType = object : TypeToken<List<Comment>>(){}.type
        return Gson().fromJson(value, listType)
    }

}