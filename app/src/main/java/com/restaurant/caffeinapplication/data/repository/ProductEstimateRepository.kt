package com.restaurant.caffeinapplication.data.repository

import android.util.Log
import com.restaurant.caffeinapplication.data.model.ResponseUpdateOrder
import com.restaurant.caffeinapplication.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductEstimateRepository {

    var apiConfig = ApiConfig()

    fun updateProductEstimate(idProduct : Int, status : String, estimateTime : Int? = null, descriptionReject : String? = null,
                              onResult : (result : ResponseUpdateOrder)-> Unit){
        apiConfig.server.updateEstimateProduct(idProduct,status,estimateTime, descriptionRejected = descriptionReject).enqueue(object : Callback<ResponseUpdateOrder>{
            override fun onResponse(call: Call<ResponseUpdateOrder>, response: Response<ResponseUpdateOrder>) {
                Log.d("REJECT-PAYLOAD", "rejectedPickup-repo: $descriptionReject")
                Log.d("REJECT-PAYLOAD", "rejectedPickup-repo: ${response.code()}")
                updateSuccess(response, onResult)
            }
            override fun onFailure(call: Call<ResponseUpdateOrder>, t: Throwable) {
                Log.d("FAILED-PRODUCT-ESTIMATE", "onFailure: ${t.message}")
            }
        })
    }

    fun updateSuccess(response : Response<ResponseUpdateOrder>, onResult: (result: ResponseUpdateOrder) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseUpdateOrder(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseUpdateOrder(message = authError)
                onResult(default)
            }
        }
    }

}