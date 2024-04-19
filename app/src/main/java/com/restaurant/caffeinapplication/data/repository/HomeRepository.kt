package com.restaurant.caffeinapplication.data.repository

import android.util.Log
import com.restaurant.caffeinapplication.data.model.ResponseNewOrder
import com.restaurant.caffeinapplication.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository {

    var apiConfig = ApiConfig()

    fun getProduct(onResult : (result : ResponseNewOrder)->Unit){
        apiConfig.server.getProduct().enqueue(object : Callback<ResponseNewOrder>{
            override fun onResponse(call: Call<ResponseNewOrder>, response: Response<ResponseNewOrder>) {
                Log.d("PRODUCT-HOME", "onCreate:S ${response.body()?.data}")
                if (response.body()?.data?.result != null){
                    Log.d("PRODUCT-HOME", "onCreate: ${response.body()!!.data!!.result}")
                }

                productSuccess(response, onResult)
            }

            override fun onFailure(call: Call<ResponseNewOrder>, t: Throwable) {
                Log.d("PRODUCT-HOME", "onCreate-SSSS: ${t.message}")
            }

        })
    }

    fun getProductByStatus(status : String,type : String?=null,orderBy : String?=null,orderType : String?=null,onResult : (result : ResponseNewOrder)->Unit){
        apiConfig.server.getProduct(status, type, orderBy, orderType).enqueue(object : Callback<ResponseNewOrder>{
            override fun onResponse(call: Call<ResponseNewOrder>, response: Response<ResponseNewOrder>) {
                productSuccess(response, onResult)
            }

            override fun onFailure(call: Call<ResponseNewOrder>, t: Throwable) {
                Log.d("PRODUCT-HOME", "onCreate: ${t.message}")
            }

        })
    }

    fun getProductByFilterOrder(starDate : String? = null, endDate : String? = null,typeOrder : String? = null,
                                orderFrom : String? = null, name : String? = null,
                                status : String? = null,type : String?=null,orderBy : String?=null,
                                orderType : String?=null,onResult : (result : ResponseNewOrder)->Unit){
            apiConfig.server.getProduct(startDate = starDate, endDate = endDate, name = name,
                orderFrom = orderFrom, typeOrder = typeOrder, orderType = orderType).enqueue(object : Callback<ResponseNewOrder>{
                override fun onResponse(call: Call<ResponseNewOrder>, response: Response<ResponseNewOrder>) {
                    Log.d("DATA-FILTER", "onCreate: ${response.code()}")
                    Log.d("PAYLOAD---", "onCreate: ${response.code()}")
                    productSuccess(response, onResult)
                }
                override fun onFailure(call: Call<ResponseNewOrder>, t: Throwable) {
                    Log.d("PRODUCT-HOME", "fail`: ${t.message}")
                }

            })
    }

    fun productSuccess(response : Response<ResponseNewOrder>,onResult: (result: ResponseNewOrder) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseNewOrder(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseNewOrder(message = authError)
                onResult(default)
            }
        }
    }

}