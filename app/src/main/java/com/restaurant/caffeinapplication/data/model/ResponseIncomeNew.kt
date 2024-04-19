package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseIncomeNew(

	@field:SerializedName("response")
	val response: List<ResponseItemVoucher?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class DateItem(

	@field:SerializedName("date")
	val date: Int? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("data")
	val data: List<DataItemVoucher?>? = null
)

data class DataItemVoucher(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("voucher_customer")
	val voucherCustomer: VoucherCustomer? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: UserVoucher? = null
)

data class VoucherCustomer(

	@field:SerializedName("voucher")
	val voucher: Voucher? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Voucher(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("total_amount")
	val totalAmount: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class MonthItem(

	@field:SerializedName("date")
	val date: List<DateItem?>? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("month")
	val month: Int? = null
)

data class UserVoucher(

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class ResponseItemVoucher(

	@field:SerializedName("total_income")
	val totalIncome: Any? = null,

	@field:SerializedName("month")
	val month: List<MonthItem?>? = null,

	@field:SerializedName("year")
	val year: Int? = null
)
