package com.restaurant.caffeinapplication.data.repository

import android.util.Log
import com.restaurant.caffeinapplication.data.model.ResponseSettingStatusStore
import com.restaurant.caffeinapplication.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingRepository {

    val apiConfig = ApiConfig()

    fun updateStatusStore(isOpen : Int, onResult : (result : ResponseSettingStatusStore)-> Unit){
        apiConfig.server.settingStatusStore(isOpen = isOpen).enqueue(object : Callback<ResponseSettingStatusStore>{
            override fun onResponse(call: Call<ResponseSettingStatusStore>, response: Response<ResponseSettingStatusStore>) {
                Log.d("SETTING-API", "updateStatusStore---: ${response.code()}")
                if (response.isSuccessful){
                    updateStatusSuccess(response, onResult)
                }else{
                    Log.d("SETTING-API", "updateStatusStore-gagal: ${response.code()}")
                    val authError = "Token Times Up"
                    val default = ResponseSettingStatusStore(message = authError)
                    onResult(default)
                }
            }

            override fun onFailure(call: Call<ResponseSettingStatusStore>, t: Throwable) {
                Log.d("SETTING-API", "updateStatusStore-failure: ${t.message}")
                val authError = "Please Check Your Connection"
                val default = ResponseSettingStatusStore(message = authError)
                onResult(default)
            }

        })
    }


    fun updateStatusSuccess(response : Response<ResponseSettingStatusStore>, onResult: (result: ResponseSettingStatusStore) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseSettingStatusStore(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseSettingStatusStore(message = authError)
                onResult(default)
            }
        }
    }

}