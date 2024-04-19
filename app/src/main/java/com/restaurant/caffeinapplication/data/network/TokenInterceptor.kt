package com.restaurant.caffeinapplication.data.network

import com.restaurant.caffeinapplication.utils.Constant
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val authToken: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${Constant.TOKEN_USER}")
            .build()
        return chain.proceed(request)
    }
}