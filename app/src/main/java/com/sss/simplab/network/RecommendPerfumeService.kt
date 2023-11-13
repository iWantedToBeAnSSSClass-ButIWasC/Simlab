package com.sss.simplab.network

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Part

interface RecommendPerfumeService {

    @POST("/receive_info")
    fun recommend(
        @Part imageFile: MultipartBody.Part
    ) : Call<Perfume>
}