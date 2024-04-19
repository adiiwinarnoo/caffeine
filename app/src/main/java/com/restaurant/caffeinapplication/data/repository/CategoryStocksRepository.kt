package com.restaurant.caffeinapplication.data.repository

import android.util.Log
import com.restaurant.caffeinapplication.data.model.ResponseCategoryStocks
import com.restaurant.caffeinapplication.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryStocksRepository {

    var apiConfig = ApiConfig()

    fun getCategoryStocks(onResult :(result : ResponseCategoryStocks)-> Unit){
        apiConfig.server.getCategoryStocks().enqueue(object : Callback<ResponseCategoryStocks>{
            override fun onResponse(call: Call<ResponseCategoryStocks>, response: Response<ResponseCategoryStocks>) {
                categoryStockSuccess(response, onResult)
            }
            override fun onFailure(call: Call<ResponseCategoryStocks>, t: Throwable) {
                Log.d("CATEGORY-STOCKS", "onFailure: ${t.message}")
            }
        })
    }

    fun categoryStockSuccess(response : Response<ResponseCategoryStocks>, onResult: (result: ResponseCategoryStocks) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseCategoryStocks(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseCategoryStocks(message = authError)
                onResult(default)
            }
        }
    }
}