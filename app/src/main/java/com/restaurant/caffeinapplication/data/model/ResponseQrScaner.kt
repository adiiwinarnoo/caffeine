package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseQrScaner(

	@field:SerializedName("message")
	val messageData: Message? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("message_error")
	val message: String? = null,

)

data class ResponseQR(

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,
)

data class DataQRScaner(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("productId")
	val productId: Int? = null,

	@field:SerializedName("voucherId")
	val voucherId: Int? = null,

	@field:SerializedName("companyId")
	val companyId: Int? = null,

	@field:SerializedName("product")
	val product: ProductQRScaner? = null,

	@field:SerializedName("voucher")
	val voucher: VoucherQRScaner? = null,

	@field:SerializedName("total_amount")
	val totalAmount: Int? = null,

	@field:SerializedName("amount_price")
	val amountPrice: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("expt_date")
	val exptDate: String? = null,

	@field:SerializedName("used_at")
	val usedAt: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("sale_price")
	val salePrice: Int? = null,

	@field:SerializedName("price_value")
	val priceValue: Int? = null,

	@field:SerializedName("sale_from_dt")
	val saleFromDt: String? = null,

	@field:SerializedName("sale_to_dt")
	val saleToDt: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class ProductQRScaner(

	@field:SerializedName("id")
	val idProduct: Int? = null,

	@field:SerializedName("name")
	val productName: String? = null,

	@field:SerializedName("type")
	val typeProduct: String? = null,

	@field:SerializedName("thumbnail")
	val thumbnailProduct: String? = null,

	@field:SerializedName("main_img")
	val mainImgProduct: String? = null,

	@field:SerializedName("type_coupon")
	val typeCoupon: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("price")
	val priceProduct: Int? = null,

	@field:SerializedName("hashtag")
	val hashtag: String? = null,

	@field:SerializedName("productCategoryId")
	val productCategoryId: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null

)

data class VoucherQRScaner(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("image")
	val imageVoucher: String? = null,
)

data class Message(

	@field:SerializedName("code_qr")
	val codeQr: String? = null,

	@field:SerializedName("data")
	val data: DataQRScaner? = null,

	@field:SerializedName("type")
	val type: String? = null
)
