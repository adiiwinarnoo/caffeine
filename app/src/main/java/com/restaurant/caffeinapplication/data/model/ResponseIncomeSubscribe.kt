package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseIncomeSubscribe(

	@field:SerializedName("response")
	val response: List<ResponseItemIncomeSubscribe?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class MonthItemIncomeSubscribe(

	@field:SerializedName("date")
	val date: List<DateItemIncomeSubscribe?>? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("month")
	val month: Int? = null
)

data class DateItemIncomeSubscribe(

	@field:SerializedName("date")
	val date: Int? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("data_subscription")
	val dataSubscription: List<DataSubscriptionItem?>? = null
)

data class DataSubscriptionItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("subscriber")
	val subscriber: Subscriber? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class ProductIncomeSubscribe(

	@field:SerializedName("price")
	val price: Int? = null
)

data class ResponseItemIncomeSubscribe(

	@field:SerializedName("month")
	val month: List<MonthItemIncomeSubscribe?>? = null,

	@field:SerializedName("year")
	val year: Int? = null
)

data class Subscription(

	@field:SerializedName("product")
	val product: ProductIncomeSubscribe? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class Subscriber(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("subscription")
	val subscription: Subscription? = null
)
