package com.example.ethysell.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.ethysell.db.DataBaseData
import com.example.ethysell.db.ProductDatabase
import com.example.ethysell.model.*
import com.example.ethysell.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.lang.Exception

class ProductRepository(private val database: ProductDatabase) {
    private val TAG = ProductRepository::class.java.simpleName
    val productResponse = MutableLiveData<String>()
    val productData = MutableLiveData<ProductContainer>()
    val createResponse = MutableLiveData<Response<Register>>()
    val loginResponse = MutableLiveData<Response<CurrentUser>>()
    val exceptionAuthResponse = MutableLiveData<String>()
    val updateResponse = MutableLiveData<String>()
    val deleteCommentResponse = MutableLiveData<String>()
    val fetchResponse = MutableLiveData<String>()
    val deleteItemResponse = MutableLiveData<String>()
    val postItemResponse = MutableLiveData<String>()
    val commentResponse = MutableLiveData<String>()


    suspend fun post(product: Data, filePath: File) {

        val nFile = File(filePath.absolutePath)
        Log.d(TAG, "post: file  $nFile")
        val type = product.type.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val name = product.name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val barcode = product.barcodeNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val description =
            product.description!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ethicalScore =
            product.ethicalScore.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val requestFile = nFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val image = MultipartBody.Part.createFormData("image", nFile.name, requestFile)
        val api = ProductApi.retrofitService
        withContext(Dispatchers.IO) {
            try {
                val response = api.postProduct(
                    type,
                    name,
                    barcode,
                    ethicalScore,
                    description,
                    image
                )

                if (response.isSuccessful){
                    Log.d(TAG, "post: Data response ${response.body()}")
                    productResponse.postValue(response.body()?.message)
                }else{
                    productResponse.postValue(response.message())
                }

                //Log.d(TAG, "post: Image $image")
            } catch (e: Exception) {
                Log.d(TAG, "post: Exception ${e.message}")
                updateResponse.postValue(e.message)
            }


        }

    }



    suspend fun updateComment(data: DataBaseData) =
        database.productDao().updateProductComment(data)


    suspend fun deleteItem(itemId: Int) {
        try {
           val response = ProductApi.retrofitService.deleteItem(itemId)
            if (response.isSuccessful){
                database.productDao().deleteItem(itemId)
                deleteItemResponse.postValue(response.body()?.message)
            }else{
                deleteItemResponse.postValue(response.message())
                Log.d(TAG, "deleteItem: ${response.message()}")
            }
        }catch (e:Exception){
            deleteItemResponse.postValue(e.message)
            Log.d(TAG, "deleteItem: ${e.message}")
        }
    }


    suspend fun update(product: Data, file: File) {
        var fileToString: String = ""
        val imageUrl = product.imageUrl
        fileToString = if (imageUrl.toString() == file.absolutePath){
            ""
        }else{
            file.absolutePath
        }

        val filePath = File(fileToString)
        val nFile = File(filePath.absolutePath)
        Log.d(TAG, "post: file  $nFile")
        val type = product.type.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val name = product.name.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val barcode = product.barcodeNo.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val description =
            product.description!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val ethicalScore =
            product.ethicalScore.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val requestFile = nFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val image = MultipartBody.Part.createFormData("image", nFile.name, requestFile)
        val api = ProductApi.retrofitService
        withContext(Dispatchers.IO) {
            Log.d(TAG, "update: ${product.id}, $type, $name, $barcode, $ethicalScore, $image}")
            try {
                val response = api.updateProduct(
                    product.id,
                    type,
                    name,
                    barcode,
                    ethicalScore,
                    image
                )
                if (response.isSuccessful){
                    updateResponse.postValue(response.body()?.message)
                }else{
                    updateResponse.postValue(response.message())
                }

                Log.d(TAG, "post: Data response ${response.body()}")
                //Log.d(TAG, "post: Image $image")
            } catch (e: Exception) {
                Log.d(TAG, "post gallery: Exception ${e.message}")
                updateResponse.postValue(e.message)

            }


        }

    }

    fun dataFromDatabase() =
        database.productDao().getAllProducts()

    suspend fun refreshData() {

        try {
            withContext(Dispatchers.IO) {
                val product = ProductApi.retrofitService.getProducts()
                if (product.isSuccessful) {
//                    fetchResponse.postValue(product.message())
                        database.productDao().clearAll()
                    val data = product.body()?.asDatabaseModel()
                    if (data != null) {
                        database.productDao().insertAll(data)
                    }
                } else {
                    fetchResponse.postValue(product.errorBody().toString())
                }
            }
        } catch (e: Exception) {
            fetchResponse.postValue(e.message)
        }

    }


    suspend fun register(name: String, email: String, password: String, role: String) {
        withContext(Dispatchers.IO) {
            try {
                val createUserResponse = ProductApi.retrofitService.createUser(
                    name = name,
                    email = email,
                    password = password,
                    role = role
                )
                createResponse.postValue(createUserResponse)
            }catch (e: Exception){
                exceptionAuthResponse.postValue(e.message)
            }

        }
    }

    suspend fun login(email: String, password: String) {
        withContext(Dispatchers.IO) {
            try {
                val loginUserResponse = ProductApi.retrofitService.loginUser(
                    email = email,
                    password = password
                )
                loginResponse.postValue(loginUserResponse)
            } catch (e: Exception) {
                exceptionAuthResponse.postValue(e.message)
            }

        }
    }

    suspend fun postComment(id: Int, comment: String, currentUser: CurrentUser, data: Data) {

        val token = currentUser.data.userInfo
        val userId = currentUser.data.userInfo.id

        withContext(Dispatchers.IO) {
            try {
                val response = ProductApi.retrofitService.postComment(
                    "Bearer $token",
                    id,
                    comment,
                    userId
                )

                if (response.isSuccessful){
                    commentResponse.postValue(response.body()?.message)
                    database.productDao().updateProductComment(data.asDatabaseData())
                }else{
                    commentResponse.postValue(response.message())
                }
            } catch (e: Exception) {
                Log.d(TAG, "postComment: ${e.message}")
                commentResponse.postValue(e.message)
            }


        }
    }

    suspend fun deleteComment(commentId: Int, isHidden: Boolean, data: Data) {


        withContext(Dispatchers.IO) {
            try {
                val response = ProductApi.retrofitService.updateComment(
                  commentId,
                    isHidden
                )
                if (response.isSuccessful){
                    deleteCommentResponse.postValue(response.body()?.message)
                    database.productDao().updateProductComment(data.asDatabaseData())
                    refreshData()
                    Log.d(TAG, "deleteComment: deleteResponse ${response.body()}")
                }else{
                    deleteCommentResponse.postValue(response.message())
                    Log.d(TAG, "deleteComment: ${response.message()}")
                }


            } catch (e: Exception) {
                Log.d(TAG, "deleteComment: ${e.message}")
                deleteCommentResponse.postValue(e.message)
            }


        }
    }

    fun getDetailProduct(itemId: Int) =
        database.productDao().getProduct(itemId)


}

