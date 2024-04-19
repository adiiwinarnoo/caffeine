package com.restaurant.caffeinapplication.data.repository

import android.util.Log
import com.restaurant.caffeinapplication.data.model.ResponseNotice
import com.restaurant.caffeinapplication.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NoticeRepository {

    var apiConfig = ApiConfig()

    fun getNotice(type : String, type_data : String, onResult: (result : ResponseNotice)-> Unit){
        apiConfig.server.getNotice(type,type_data).enqueue(object : Callback<ResponseNotice>{
            override fun onResponse(call: Call<ResponseNotice>, response: Response<ResponseNotice>) {
                getNoticeSuccess(response, onResult)
            }
            override fun onFailure(call: Call<ResponseNotice>, t: Throwable) {
                Log.d("NOTICE-FAILED", "onFailure: ${t.message}")
            }
        })
    }

    fun getNoticeSuccess(response : Response<ResponseNotice>,onResult: (result: ResponseNotice) -> Unit){
        when(response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseNotice(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseNotice(message = authError)
                onResult(default)
            }
        }
    }

}