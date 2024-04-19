package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseUpdateOrder(

	@field:SerializedName("data")
	val data: DataUpdateOrder? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DataUpdateOrder(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("companyid")
	val companyid: Int? = null,

	@field:SerializedName("status_payment")
	val statusPayment: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("estimated_time")
	val estimatedTime: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
