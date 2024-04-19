package com.restaurant.caffeinapplication.data.repository

import android.util.Log
import com.restaurant.caffeinapplication.data.model.ResponseLogin
import com.restaurant.caffeinapplication.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {

    var apiConfig = ApiConfig()

    fun login(accountId : String, accountPwd : String,fcmToken: String, onResult : (result : ResponseLogin) -> Unit){
        apiConfig.server.login(accountId,accountPwd,fcmToken).enqueue(object : Callback<ResponseLogin>{
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                Log.d("LOGIN-FAILED", "onResponse-code: ${response.code()}")
                Log.d("LOGIN-FAILED", "onResponse-message: ${response.message()}")
                loginSuccess(response, onResult)
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                Log.d("LOGIN-FAILED", "onFailure: ${t.message}")
            }

        })
    }

    fun loginSuccess(response : Response<ResponseLogin>, onResult : (result : ResponseLogin)->Unit){
        when(response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val errorAuth = "Password Wrong!"
                val default = ResponseLogin(message = errorAuth)
                onResult(default)
            }
            404 -> {
                val errorAuth = "account_id dosnt exist!"
                val default = ResponseLogin(message = errorAuth)
                onResult(default)
            }
        }
    }

}