package com.example.plant

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    // 게시물 저장 메소드
    @Multipart
    @POST("insertpost.php") // 이미지를 업로드하는 서버의 URL
    suspend fun insertpost(
        @Part image: MultipartBody.Part,
        @Part("board_type") board_type: RequestBody,
        @Part("post_title") post_title: RequestBody,
        @Part("post_content") post_content: RequestBody,
        @Part("post_writer") post_writer: RequestBody
        //@Part("post_date") post_date: RequestBody
    ): Response<String>

    //게시물 수정 메소드
    @Multipart
    @POST("editpost.php") // 이미지를 업로드하는 서버의 URL
    suspend fun editpost(
        @Part image: MultipartBody.Part,
        @Part("post_num") post_num: RequestBody,
        @Part("board_type") board_type: RequestBody,
        @Part("post_title") post_title: RequestBody,
        @Part("post_content") post_content: RequestBody,
        @Part("post_writer") post_writer: RequestBody
        //@Part("post_date") post_date: RequestBody
    ): Response<String>

    //게시물 삭제 메소드
//    @POST("deletepost.php")
//    fun deletePost(@Query("post_num") post_num: Int): Call<ResponseBody>

    // 이미지를 가져오기 위한 메소드
    @GET("getimage.php") // 이미지를 가져오는 서버의 엔드포인트 URL
    fun getImageById(@Query("id") id: Int): Call<ResponseBody>

    @Multipart
    @POST("image_upload.php")
    fun sendImage(
        @Part imageFile: MultipartBody.Part
    ): Call<ImageUploadResponse>

}
data class ImageUploadResponse(val imageUrl: String)