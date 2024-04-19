package com.restaurant.caffeinapplication.data.network

import com.restaurant.caffeinapplication.utils.Constant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {

    var tokenAuth = TokenInterceptor(Constant.TOKEN_USER)
    var client = OkHttpClient.Builder().addInterceptor(tokenAuth).build()

    private val retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(Constant.APP_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var server : ApiService = retrofit.create(ApiService::class.java)
}