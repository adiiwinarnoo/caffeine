package com.restaurant.caffeinapplication.data.repository

import android.util.Log
import com.restaurant.caffeinapplication.data.model.*
import com.restaurant.caffeinapplication.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IncomeRepository {

    val apiConfig = ApiConfig()

    fun getAllVoucher(starDate : String? = null, endDate : String? = null, onResult : (result : ResponseIncomeNew)-> Unit){
        apiConfig.server.getAllVoucher(startDate = starDate, endDate = endDate).enqueue(object : Callback<ResponseIncomeNew>{
            override fun onResponse(call: Call<ResponseIncomeNew>, response: Response<ResponseIncomeNew>) {
                if (response.isSuccessful){
                    Log.d("API-INCOME", "onResponse: ${response.body()?.response}")
                    allVoucherSuccess(response, onResult)
                }else{
                    val authError = "Error"
                    val default = ResponseIncomeNew(message = authError)
                    onResult(default)
                }
            }

            override fun onFailure(call: Call<ResponseIncomeNew>, t: Throwable) {
                Log.d("API-INCOME", "onFailure: ${t.message}")
            }
        })
    }

    fun getAllCoupon(starDate : String? = null, endDate : String? = null, onResult : (result : ResponseIncomeCoupon)-> Unit){
        apiConfig.server.getAllCoupon(startDate = starDate, endDate = endDate).enqueue(object : Callback<ResponseIncomeCoupon>{
            override fun onResponse(call: Call<ResponseIncomeCoupon>, response: Response<ResponseIncomeCoupon>) {
                if (response.isSuccessful){
                    Log.d("API-INCOME", "onResponse: ${response.body()?.response}")
                    allCouponSuccess(response, onResult)
                }else{
                    val authError = "Error"
                    val default = ResponseIncomeCoupon(message = authError)
                    onResult(default)
                }
            }

            override fun onFailure(call: Call<ResponseIncomeCoupon>, t: Throwable) {
                Log.d("API-INCOME", "onFailure: ${t.message}")
            }
        })
    }

    fun getAllSubscribe(starDate : String? = null, endDate : String? = null, onResult : (result : ResponseIncomeSubscribe)-> Unit){
        apiConfig.server.getAllSubscribe(startDate = starDate, endDate = endDate).enqueue(object : Callback<ResponseIncomeSubscribe>{
            override fun onResponse(call: Call<ResponseIncomeSubscribe>, response: Response<ResponseIncomeSubscribe>) {
                if (response.isSuccessful){
                    Log.d("API-INCOME", "onResponse: ${response.body()?.response}")
                    allSubscribeSuccess(response, onResult)
                }else{
                    val authError = "Error"
                    val default = ResponseIncomeSubscribe(message = authError)
                    onResult(default)
                }
            }

            override fun onFailure(call: Call<ResponseIncomeSubscribe>, t: Throwable) {
                Log.d("API-INCOME", "onFailure: ${t.message}")
            }
        })
    }

    fun getOrderHistory(starDate : String? = null, endDate : String? = null,typeOrder : String? = null, onResult : (result : ResponseIncomeOrder)-> Unit){
        apiConfig.server.getOrderHistory(startDate = starDate, endDate = endDate, typeOrder = typeOrder).enqueue(object : Callback<ResponseIncomeOrder>{
            override fun onResponse(call: Call<ResponseIncomeOrder>, response: Response<ResponseIncomeOrder>) {
                Log.d("DATA-INCOME", "onResponse-ORDER: ${response.code()}")
                allHistorySuccess(response, onResult)
            }

            override fun onFailure(call: Call<ResponseIncomeOrder>, t: Throwable) {
                Log.d("DATA-INCOME", "onFailure-HISTORY: ${t.message}")
            }

        })
    }

    fun allVoucherSuccess(response : Response<ResponseIncomeNew>, onResult: (result: ResponseIncomeNew) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseIncomeNew(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseIncomeNew(message = authError)
                onResult(default)
            }
        }
    }

    fun allHistorySuccess(response : Response<ResponseIncomeOrder>, onResult: (result: ResponseIncomeOrder) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseIncomeOrder(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseIncomeOrder(message = authError)
                onResult(default)
            }
        }
    }

    fun allCouponSuccess(response : Response<ResponseIncomeCoupon>, onResult: (result: ResponseIncomeCoupon) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseIncomeCoupon(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseIncomeCoupon(message = authError)
                onResult(default)
            }
        }
    }

    fun allSubscribeSuccess(response : Response<ResponseIncomeSubscribe>, onResult: (result: ResponseIncomeSubscribe) -> Unit){
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseIncomeSubscribe(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseIncomeSubscribe(message = authError)
                onResult(default)
            }
        }
    }



}