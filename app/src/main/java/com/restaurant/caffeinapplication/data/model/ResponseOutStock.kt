package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseOutStock(

	@field:SerializedName("data")
	val data: DataOutOfStock? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DataOutOfStock(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("companyId")
	val companyId: Int? = null,

	@field:SerializedName("productId")
	val productId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("valid_til")
	val validTil: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class UpdateOutStock (
	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
data class DeleteRequestBody(
	@field:SerializedName("productId") val productId: Int
)

data class UploadScanQr(

	@field:SerializedName("code_qr")
	val codeQr: String? = null,

	@field:SerializedName("total_amount")
	val totalAmount: Int? = null,
)
