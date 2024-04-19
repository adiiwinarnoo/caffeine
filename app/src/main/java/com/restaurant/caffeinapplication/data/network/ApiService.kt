package com.restaurant.caffeinapplication.data.network

import com.restaurant.caffeinapplication.data.model.*
import retrofit2.http.*

interface ApiService {

    @PATCH("/api/order/company/update/{id}")
    @FormUrlEncoded
    fun updateEstimateProduct(
        @Path("id") idProduct : Int,
        @Field("status") status : String = "making",
        @Field("estimated_time") estimatedTime : Int? = null,
        @Field("description_rejected") descriptionRejected : String? = null
    ) : retrofit2.Call<ResponseUpdateOrder>

    @PATCH("/api/company/setting")
    @FormUrlEncoded
    fun settingStatusStore(
        @Field("is_open") isOpen : Int? = null
    ) : retrofit2.Call<ResponseSettingStatusStore>

    @FormUrlEncoded
    @POST("/api/company/login")
    fun login(
        @Field("account_id") accountId : String,
        @Field("account_pwd") accountPwd : String,
        @Field("fcm_token") fcmToken : String,
    ) : retrofit2.Call<ResponseLogin>

    @FormUrlEncoded
    @POST("/api/order/scan/preview")
    fun qrScanPreview(
        @Field("code_qr") codeQr : String,
    ) : retrofit2.Call<ResponseQrScaner>

    @FormUrlEncoded
    @POST("/api/order/scan")
    fun qrScan(
        @Field("code_qr") codeQr : String,
        @Field("total_amount") totalAmount : Int? = null
    ) : retrofit2.Call<ResponseQR>

    @FormUrlEncoded
    @POST("/api/productstock/company")
    fun outStock(
        @Field("productId") productId : Int,
    ) : retrofit2.Call<ResponseOutStock>

    @GET("/api/order/company/my")
    fun getProduct(
        @Query("status") status : String? = null,
        @Query("type") type : String? = null,
        @Query("order_by") orderBy : String? = null,
        @Query("order_type") orderType : String? = null,
        @Query("startdate") startDate : String? = null,
        @Query("enddate") endDate : String? = null,
        @Query("name") name : String? = null,
        @Query("order_from") orderFrom : String? = null,
        @Query("type_order") typeOrder : String? = null,
    ) : retrofit2.Call<ResponseNewOrder>

    @GET("/api/product/company")
    fun getProductStocks(
        @Query("productCategoryId") productCategoryId : Int? = null,
        @Query("search") search : String? = null,
    ) : retrofit2.Call<ResponseProductStocks>

    @GET("/api/notice/company")
    fun getNotice(
        @Query("type") type : String?,
        @Query("type_data") typeData : String?,
    ) : retrofit2.Call<ResponseNotice>

    @GET("/api/category")
    fun getCategoryStocks() : retrofit2.Call<ResponseCategoryStocks>

    @GET("/api/order/voucher")
    fun getAllVoucher(
        @Query("startdate") startDate: String? = null,
        @Query("enddate") endDate: String? = null,
    ) : retrofit2.Call<ResponseIncomeNew>

    @GET("/api/order/history")
    fun getOrderHistory(
        @Query("startdate") startDate: String? = null,
        @Query("enddate") endDate: String? = null,
        @Query("type_order") typeOrder: String? = null,
    ) : retrofit2.Call<ResponseIncomeOrder>

    @GET("/api/order/coupon")
    fun getAllCoupon(
        @Query("startdate") startDate: String? = null,
        @Query("enddate") endDate: String? = null,
    ) : retrofit2.Call<ResponseIncomeCoupon>

    @GET("/api/order/subscription")
    fun getAllSubscribe(
        @Query("startdate") startDate: String? = null,
        @Query("enddate") endDate: String? = null,
    ) : retrofit2.Call<ResponseIncomeSubscribe>

    @HTTP(method = "DELETE", path = "/api/productstock/company/", hasBody = true)
    fun deleteOutStock(
       @Body requestBody: DeleteRequestBody
    ) : retrofit2.Call<UpdateOutStock>

    @HTTP(method = "POST", path = "/api/order/scan", hasBody = true)
    fun scanQR(
        @Body requestBody: UploadScanQr
    ) : retrofit2.Call<ResponseQrScaner>

}