package com.example.ethysell.databindingutils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ethysell.R
import com.example.ethysell.model.Data
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL



@BindingAdapter("bindProductImage")
fun bindProductImageView(imageView: ImageView, product: Data){

//    val image = product.imageUrl
//    val url = URL(image)
//    val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
//    connection.doInput = true
//    connection.connect()
//    val input: InputStream = connection.inputStream
//    val myBitmap = BitmapFactory.decodeStream(input)


    val requestOptions = RequestOptions()
    requestOptions.apply {
        this.error(R.drawable.ic_broken_image)
        this.placeholder(R.drawable.loading_animation)
    }

    Glide.with(imageView)
        .load(product.imageUrl)
        .apply(requestOptions)
        .into(imageView)

}


