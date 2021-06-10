package com.example.ethysell.network

import com.example.ethysell.const.BASE_URL
import com.example.ethysell.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ApiWorker.gsonConverter)
    .client(ApiWorker.client)
    .baseUrl(BASE_URL)
    .build()

interface ProductApiService {


    @Multipart
    @POST("api/items")
    suspend fun postProduct(
        @Part("type") type: RequestBody,
        @Part("name") name: RequestBody,
        @Part("barcodeNo") barcodeNo: RequestBody,
        @Part("ethicalScore") ethicalScore: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<PostResponse>

    @Multipart
    @POST("api/items/{id}/update")
    suspend fun updateProduct(
        @Path("id") itemId: Int,
        @Part("type") type: RequestBody,
        @Part("name") name: RequestBody,
        @Part("barcodeNo") barcodeNo: RequestBody,
        @Part("ethicalScore") ethicalScore: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<PostResponse>


    @GET("api/items")
    suspend fun getProducts():Response<ProductContainer>

    @FormUrlEncoded
    @POST("api/user/register")
    suspend fun createUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("role") role: String,
        @Field("password") password: String
    ): Response<Register>


    @FormUrlEncoded
    @POST("api/user/authenticate")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<CurrentUser>


    @FormUrlEncoded
    @POST("/api/post-review")
    suspend fun postComment(
        @Header("Authorization") token: String,
        @Field("itemId") itemId: Int,
        @Field("comment") comment: String,
        @Field("userId") userId: Int
    ): Response<ProductComment>

    @FormUrlEncoded
    @POST("/api/update-comment")
    suspend fun updateComment(
        @Field("commentId") commentId: Int,
        @Field("isHidden") isHidden: Boolean
    ): Response<ProductComment>


    @DELETE("/api/items/{itemId}")
    suspend fun  deleteItem(
        @Path("itemId") id: Int
    ): Response<ItemDelete>




}

object ProductApi {
    val retrofitService: ProductApiService by lazy {
        retrofit.create(ProductApiService::class.java)
    }
}



