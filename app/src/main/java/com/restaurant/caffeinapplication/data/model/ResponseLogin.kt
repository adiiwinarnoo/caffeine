package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseLogin(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("token")
	val token: String? = null
)

data class Data(

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("reg_no")
	val regNo: String? = null,

	@field:SerializedName("commision_rate")
	val commisionRate: Int? = null,

	@field:SerializedName("rating")
	val rating: Int? = null,

	@field:SerializedName("delivery_company")
	val deliveryCompany: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("account_pwd")
	val accountPwd: String? = null,

	@field:SerializedName("long")
	val jsonMemberLong: Double? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("account_id")
	val accountId: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("address_street")
	val addressStreet: String? = null,

	@field:SerializedName("fcm_token")
	val fcmToken: Any? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("bank_name")
	val bankName: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("lat")
	val lat: Double? = null,

	@field:SerializedName("bank_account")
	val bankAccount: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("is_open")
	val isOpen: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
