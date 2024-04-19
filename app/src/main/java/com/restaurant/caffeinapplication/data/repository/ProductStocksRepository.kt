package com.restaurant.caffeinapplication.data.repository

import android.util.Log
import com.restaurant.caffeinapplication.data.model.*
import com.restaurant.caffeinapplication.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductStocksRepository {

    var apiConfig = ApiConfig()

    fun getProductStocks(onResult : (result : ResponseProductStocks)-> Unit){
        apiConfig.server.getProductStocks().enqueue(object : Callback<ResponseProductStocks>{
            override fun onResponse(call: Call<ResponseProductStocks>, response: Response<ResponseProductStocks>) {
                productStockSuccess(response, onResult)
            }
            override fun onFailure(call: Call<ResponseProductStocks>, t: Throwable) {
                Log.d("PRODUCT-STOCKS", "onFailure: ${t.message}")
            }
        })
    }
    fun getProductStocksByCategorySearch(idCategoryProduct : Int? = null,searchText: String?=null,onResult : (result : ResponseProductStocks)-> Unit){
        apiConfig.server.getProductStocks(idCategoryProduct,searchText).enqueue(object : Callback<ResponseProductStocks>{
            override fun onResponse(call: Call<ResponseProductStocks>, response: Response<ResponseProductStocks>) {
                productStockSuccess(response, onResult)
            }
            override fun onFailure(call: Call<ResponseProductStocks>, t: Throwable) {
                Log.d("PRODUCT-STOCKS", "onFailure: ${t.message}")
            }
        })
    }

    fun updateOutStock(idProduct : Int, onResult : (result : ResponseOutStock)-> Unit){
        apiConfig.server.outStock(idProduct).enqueue(object : Callback<ResponseOutStock>{
            override fun onResponse(call: Call<ResponseOutStock>, response: Response<ResponseOutStock>) {
                updateOutStockSuccess(response,onResult)
            }
            override fun onFailure(call: Call<ResponseOutStock>, t: Throwable) {
                Log.d("PRODUCT-STOCKS-UPDATE", "onFailure: ${t.message}")
            }

        })
    }

    fun deleteOutStock(idProduct: Int, onResult: (result: UpdateOutStock) -> Unit){
        val productId = DeleteRequestBody(idProduct)
        apiConfig.server.deleteOutStock(productId).enqueue(object : Callback<UpdateOutStock>{
            override fun onResponse(call: Call<UpdateOutStock>, response: Response<UpdateOutStock>) {
                Log.d("PRODUCT-STOCKS-DELETE", "onFailure: ${response.code()}")
                deleteOutStockSuccess(response, onResult)
            }
            override fun onFailure(call: Call<UpdateOutStock>, t: Throwable) {
                Log.d("PRODUCT-STOCKS-DELETE", "onFailure: ${t.message}")
            }

        })

    }


    fun productStockSuccess(response : Response<ResponseProductStocks>, onResult: (result: ResponseProductStocks) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseProductStocks(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseProductStocks(message = authError)
                onResult(default)
            }
        }
    }

    fun updateOutStockSuccess(response : Response<ResponseOutStock>, onResult: (result: ResponseOutStock) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseOutStock(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseOutStock(message = authError)
                onResult(default)
            }
        }
    }
    fun deleteOutStockSuccess(response : Response<UpdateOutStock>, onResult: (result: UpdateOutStock) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = UpdateOutStock(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = UpdateOutStock(message = authError)
                onResult(default)
            }
        }
    }
}