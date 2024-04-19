package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseProductStocks(

	@field:SerializedName("data")
	val data: DataStocks? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class MetadataStocks(

	@field:SerializedName("totalData")
	val totalData: Int? = null,

	@field:SerializedName("totalPage")
	val totalPage: Int? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null
)

data class ProductCategory(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("img")
	val img: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class ProductOutStocksItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("companyId")
	val companyId: Int? = null,

	@field:SerializedName("productId")
	val productId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("valid_til")
	val validTil: Any? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class ResultItemStocks(

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("product_out_stocks")
	val productOutStocks: List<Any?>? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("deleted_at")
	val deletedAt: Any? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("productCategoryId")
	val productCategoryId: Int? = null,

	@field:SerializedName("price")
	val price: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("main_img")
	val mainImg: String? = null,

	@field:SerializedName("type_coupon")
	val typeCoupon: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("stock")
	val stock: Int? = null,

	@field:SerializedName("product_category")
	val productCategory: ProductCategory? = null,

	@field:SerializedName("hashtag")
	val hashtag: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DataStocks(

	@field:SerializedName("result")
	val result: List<ResultItemStocks?>? = null,

	@field:SerializedName("metadata")
	val metadata: MetadataStocks? = null
)
