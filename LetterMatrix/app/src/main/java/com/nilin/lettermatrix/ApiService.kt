package com.nilin.lettermatrix

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("api/round/{session_id}/")
    suspend fun getRound(@Path("session_id") sessionId: String): Response<RoundData>

    @POST("api/session/{session_id}/stop/")
    suspend fun stopSession(@Path("session_id") sessionId: String): Response<Unit>

    companion object {
        private var BASE_URL = "http://192.168.1.137:8000/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}