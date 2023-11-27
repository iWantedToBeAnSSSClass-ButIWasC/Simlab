package com.sss.simplab.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RecommendPerfumeService {
    @Multipart
    @POST("/receive_info")
    fun recommend(
        @Part image: MultipartBody.Part
    ) : Call<Perfume>
}