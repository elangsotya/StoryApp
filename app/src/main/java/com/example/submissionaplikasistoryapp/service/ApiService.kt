package com.example.submissionaplikasistoryapp.service

import com.example.submissionaplikasistoryapp.response.*
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/v1/register")
    fun doRegister(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/v1/login")
    fun doLogin(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/v1/stories/{id}")
    fun getStoryDetail(
        @Header("Authorization") token: String?,
        @Path("id") id: String?
    ): Call<DetailStoryResponse>

    @GET("/v1/stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int=0
    ): GetStoriesResponse

    @GET("/v1/stories")
    fun getStoryMaps(
        @Header("Authorization") token: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Int=0
    ): Call<GetStoriesResponse>

    @Multipart
    @POST("/v1/stories")
    fun uploadImage(
        @Header("Authorization") token: String?,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double?,
        @Part("lon") lon: Double?
    ): Call<FileUploadResponse>
}

data class RegisterRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)

data class LoginRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)