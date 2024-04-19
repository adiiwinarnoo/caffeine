package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseProductHome(

	@field:SerializedName("data")
	val data: DataProduct? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class Product(

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class OptionlistCartsItem(

	@field:SerializedName("product_option_list")
	val productOptionList: ProductOptionList? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("value")
	val value: Int? = null
)

data class OptionCartsProduct(

	@field:SerializedName("product_option")
	val productOptionList: ProductOptionList? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("value")
	val value: Int? = null
)

data class DataProduct(

	@field:SerializedName("result")
	val result: List<ResultItem?>? = null,

	@field:SerializedName("metadata")
	val metadata: Metadata? = null
)

data class Payment(

	@field:SerializedName("payment_type")
	val paymentType: String? = null,

	@field:SerializedName("third_party_id")
	val thirdPartyId: String? = null,

	@field:SerializedName("transaction")
	val transaction: String? = null
)

data class Company(

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class Cart(

	@field:SerializedName("product")
	val product: Product? = null,

	@field:SerializedName("company")
	val company: Company? = null,

	@field:SerializedName("product_and_option_total_price")
	val productAndOptionTotalPrice: Int? = null,

	@field:SerializedName("optionlist_carts")
	val optionlistCarts: List<OptionlistCartsItem?>? = null,

	@field:SerializedName("cart_optional_products")
	val cartOptionalProducts: List<OptionCartsProduct?>? = null,


)

data class AddressUser(

	@field:SerializedName("address")
	val address: String? = null
)

data class ProductOptionList(

	@field:SerializedName("name")
	val name: String? = null
)

data class ChartOrdersItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("cartId")
	val cartId: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("cart")
	val cart: Cart? = null
)

data class User(

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class Metadata(

	@field:SerializedName("totalData")
	val totalData: Int? = null,

	@field:SerializedName("totalPage")
	val totalPage: Int? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null
)

data class ResultItem(

	@field:SerializedName("total_gross")
	val totalGross: Int? = null,

	@field:SerializedName("total_price")
	val totalPrice: Int? = null,

	@field:SerializedName("chart_orders")
	val chartOrders: List<ChartOrdersItem?>? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("order_voucher_histories")
	val orderVoucherHistories: List<Any?>? = null,

	@field:SerializedName("address_user")
	val addressUser: AddressUser? = null,

	@field:SerializedName("payment")
	val payment: Payment? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("delivery_price")
	val deliveryPrice: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("estimated_time")
	val estimatedTime: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
