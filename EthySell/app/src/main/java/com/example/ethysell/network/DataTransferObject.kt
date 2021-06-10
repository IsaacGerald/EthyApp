package com.example.ethysell.network

import com.example.ethysell.db.DataBaseData
import com.example.ethysell.model.Data
import com.google.gson.annotations.JsonAdapter
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductContainer(val data: List<Data>)

fun ProductContainer.asDomainModel(): List<Data> {
    return data.map {
        Data(
            barcodeNo = it.barcodeNo,
            comments = it.comments,
            description = it.description,
            ethicalScore = it.ethicalScore,
            id = it.id,
            imageUrl = it.imageUrl,
            name = it.name,
            type = it.type
        )
    }

}

fun ProductContainer.asDatabaseModel(): List<DataBaseData> {
    return data.map {
        DataBaseData(
            barcodeNo = it.barcodeNo,
            comments = it.comments,
            description = it.description,
            ethicalScore = it.ethicalScore,
            id = it.id,
            imageUrl = it.imageUrl,
            name = it.name,
            type = it.type
        )
    }

}

fun List<DataBaseData>.asDomainData(): List<Data> {

    return map {
        Data(
            id = it.id,
            barcodeNo = it.barcodeNo,
            comments = it.comments,
            description = it.description,
            ethicalScore = it.ethicalScore,
            imageUrl = it.imageUrl,
            name = it.name,
            type = it.type
        )
    }
}

fun DataBaseData.asDomainData(): Data{
    return Data(
        id = id,
        name = name,
        type = type,
        barcodeNo = barcodeNo,
        imageUrl = imageUrl,
        description = description,
        comments = comments,
        ethicalScore = ethicalScore
    )
}

fun Data.asDatabaseData(): DataBaseData {
    return DataBaseData(
        id = id,
        barcodeNo = barcodeNo,
        name = name,
        comments = comments,
        description = description,
        ethicalScore = ethicalScore,
        imageUrl = imageUrl,
        type = type
    )
}



