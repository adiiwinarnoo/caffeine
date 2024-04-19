package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseCategoryStocks(

	@field:SerializedName("data")
	val data: DataCategoryStocks? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DataCategoryStocks(

	@field:SerializedName("result")
	val result: List<ResultItemCategoryStocks?>? = null,

	@field:SerializedName("metadata")
	val metadata: MetadataCategoryStocks? = null
)

data class MetadataCategoryStocks(

	@field:SerializedName("totalData")
	val totalData: Int? = null,

	@field:SerializedName("totalPage")
	val totalPage: Int? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null
)

data class ResultItemCategoryStocks(

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
	val updatedAt: String? = null,

	var isSelected: Boolean = false
)
