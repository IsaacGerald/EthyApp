package com.example.ethysell.addProduct

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*



class MediaHelper {
    private val TAG = MediaHelper::class.java.simpleName

    var namaFile = ""
    var fileUri = Uri.parse("")
    val RC_CAMERA = 100


    fun getMyFileName(): String{
        return this.namaFile
    }

    fun getRcCamera(): Int{
        return this.RC_CAMERA
    }

    fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ), "appx06"
        )
        if (!mediaStorageDir.exists())
            if (!mediaStorageDir.mkdir()) {
                Log.d(TAG, "getOutputMedia: mkdir")
            }
        val mediaFile = File(mediaStorageDir.path + File.separator + this.namaFile)
        return mediaFile
    }

    fun getOutPutMediaFileUri(): Uri {
        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        this.namaFile = "DC_$timestamp.jpg"
        this.fileUri = Uri.fromFile(getOutputMediaFile())
        return this.fileUri

    }

    fun bitmapToString(bmp: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getBitmapToString(imV: ImageView, uri: Uri): String {
        var bmp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        bmp = BitmapFactory.decodeFile(this.fileUri.path)
        val dim = 720
        if (bmp.height > bmp.width) {
            bmp = Bitmap.createScaledBitmap(bmp, (bmp.width * dim).div(bmp.height), dim, true)
        } else {
            bmp = Bitmap.createScaledBitmap(bmp, dim, (bmp.height * dim).div(bmp.width), true)
        }
        imV.setImageBitmap(bmp)
        return bitmapToString(bmp)
        return ""
    }


}