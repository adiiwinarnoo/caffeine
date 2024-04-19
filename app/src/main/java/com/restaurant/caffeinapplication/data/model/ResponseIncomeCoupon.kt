package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseIncomeCoupon(

	@field:SerializedName("response")
	val response: List<ResponseItemIncomeCoupon?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DateItemIncomeCoupon(

	@field:SerializedName("date")
	val date: Int? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("data_coupon")
	val dataCoupon: List<DataCouponItem?>? = null
)

data class MonthItemIncomeCoupon(

	@field:SerializedName("date")
	val date: List<DateItemIncomeCoupon?>? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("month")
	val month: Int? = null
)

data class ProductIncomeCoupon(

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class ResponseItemIncomeCoupon(

	@field:SerializedName("month")
	val month: List<MonthItemIncomeCoupon?>? = null,

	@field:SerializedName("year")
	val year: Int? = null
)

data class DataCouponItem(

	@field:SerializedName("product")
	val product: ProductIncomeCoupon? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
