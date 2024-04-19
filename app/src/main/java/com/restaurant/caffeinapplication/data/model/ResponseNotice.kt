package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseNotice(

	@field:SerializedName("data")
	val data: DataNoticeItem? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DataNoticeItem(

	@field:SerializedName("result")
	val result: List<ResultItemNotice?>? = null,

	@field:SerializedName("metadata")
	val metadata: MetadataNoticeItem? = null
)

data class MetadataNoticeItem(

	@field:SerializedName("totalData")
	val totalData: Int? = null,

	@field:SerializedName("totalPage")
	val totalPage: Int? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null
)

data class ResultItemNotice(

	@field:SerializedName("is_read")
	val isRead: Boolean? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("click_cnt")
	val clickCnt: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type_content")
	val typeContent: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("is_top")
	val isTop: Boolean? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	var dateString : String? = null
)
