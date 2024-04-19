package com.restaurant.caffeinapplication.data.repository

import android.util.Log
import com.restaurant.caffeinapplication.data.model.ResponseQR
import com.restaurant.caffeinapplication.data.model.ResponseQrScaner
import com.restaurant.caffeinapplication.data.model.UploadScanQr
import com.restaurant.caffeinapplication.data.network.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QrScanRepository {

    val apiConfig = ApiConfig()

    fun qrScaner(codeQr: String, onResult : (result : ResponseQrScaner)-> Unit){
        apiConfig.server.qrScanPreview(codeQr).enqueue(object : Callback<ResponseQrScaner>{
            override fun onResponse(call: Call<ResponseQrScaner>, response: Response<ResponseQrScaner>) {
                if (response.isSuccessful){
                    qrScanSuccess(response, onResult)
                }else{
                    Log.d("API-SCAN", "onResponse: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseQrScaner>, t: Throwable) {
                Log.d("API-SCAN", "onResponse: ${t.message}")
            }

        })
    }

    fun qrScannerCode(codeQr: String, totalAmount : Int? = null, onResult: (result: ResponseQR) -> Unit){
        val bodyData = UploadScanQr(codeQr, totalAmount)
        apiConfig.server.qrScan(codeQr = codeQr, totalAmount = totalAmount).enqueue(object : Callback<ResponseQR>{
            override fun onResponse(call: Call<ResponseQR>, response: Response<ResponseQR>) {
                Log.d("CODE-SCAN", "qrScanSuccess-CODE-CODE: ${response.code()}")
                if (response.isSuccessful){
                    Log.d("CODE-SCAN-MESSAGE", "qrScanSuccess-CODE: ${response.body()?.message}")
                    successScan(response, onResult)
                }else{
                    successScan(response, onResult)
                }
            }

            override fun onFailure(call: Call<ResponseQR>, t: Throwable) {
                Log.d("API-SCAN", "onResponse-CODE: ${t.message}")
            }

        })
    }

    fun qrScanSuccess(response : Response<ResponseQrScaner>, onResult: (result: ResponseQrScaner) -> Unit){
        Log.d("PESAN-QR", "qrScanSuccess--SCAN-SUKSES: ${response.code()}")
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseQrScaner(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseQrScaner(message = authError)
                onResult(default)
            }
            500 -> {
                val authError = "Coupon or Voucher Already Claimed"
                val default = ResponseQrScaner(message = authError)
                onResult(default)
            }
        }
    }

    fun successScan(response : Response<ResponseQR>, onResult: (result: ResponseQR) -> Unit){
        Log.d("PESAN-QR", "qrScanSuccess--SCAN-SUKSES: ${response.code()}")
        when (response.code()){
            200 -> {
                onResult(response.body()!!)
            }
            400 -> {
                val authError = "Total Amount Cannot Be More Than Voucher Amount"
                val default = ResponseQR(message = authError)
                onResult(default)
            }
            401 -> {
                val authError = "Token Times Up"
                val default = ResponseQR(message = authError)
                onResult(default)
            }
            403 -> {
                val authError = "you dont have access!"
                val default = ResponseQR(message = authError)
                onResult(default)
            }
            500 -> {
                val authError = "Coupon or Voucher Already Claimed"
                val default = ResponseQR(message = authError)
                onResult(default)
            }
        }
    }

}