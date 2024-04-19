package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseNewOrder(

	@field:SerializedName("data")
	val data: DataNewOrder? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class CartNewOrder(

	@field:SerializedName("product")
	val product: ProductNewOrder? = null,

	@field:SerializedName("company")
	val company: CompanyNewOrder? = null,

	@field:SerializedName("product_and_option_total_price")
	val productAndOptionTotalPrice: Int? = null,

	@field:SerializedName("cart_optional_products")
	val cartOptionalProducts: List<CartOptionalProductsItem?>? = null
)

data class PaymentNewOrder(

	@field:SerializedName("payment_type")
	val paymentType: String? = null,

	@field:SerializedName("third_party_id")
	val thirdPartyId: String? = null,

	@field:SerializedName("transaction")
	val transaction: String? = null
)

data class DataNewOrder(

	@field:SerializedName("result")
	val result: List<ResultItemNewOrder?>? = null,

	@field:SerializedName("metadata")
	val metadata: MetadataNewOrder? = null
)

data class CompanyNewOrder(

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class UserNewOrder(

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class ResultItemNewOrder(

	@field:SerializedName("total_gross")
	val totalGross: Int? = null,

	@field:SerializedName("total_price")
	val totalPrice: Int? = null,

	@field:SerializedName("chart_orders")
	val chartOrders: List<ChartOrdersItemNewOrder?>? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("status_payment")
	val statusPayment: Boolean? = null,

	@field:SerializedName("order_voucher_histories")
	val orderVoucherHistories: List<VoucherHistories>? = null,

	@field:SerializedName("address_user")
	val addressUser: AddressUserNewOrder? = null,

	@field:SerializedName("payment")
	val payment: Payment? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("order_type")
	val orderType: String? = null,

	@field:SerializedName("user")
	val user: UserNewOrder? = null,

	@field:SerializedName("delivery_price")
	val deliveryPrice: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("estimated_time")
	val estimatedTime: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
data class VoucherHistories(
	@field:SerializedName("voucher_name")
	val voucherName: String? = null,

	@field:SerializedName("amount")
	val voucherAmount: Int? = null,
)

data class ChartOrdersItemNewOrder(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("orderId")
	val orderId: Int? = null,

	@field:SerializedName("cartId")
	val cartId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("cart")
	val cart: CartNewOrder? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class OptionlistCartsItemNewOrder(

	@field:SerializedName("product_option_list")
	val productOptionList: ProductOptionListNewOrder? = null,

	@field:SerializedName("value")
	val value: Int? = null
)

data class MetadataNewOrder(

	@field:SerializedName("totalData")
	val totalData: Int? = null,

	@field:SerializedName("totalPage")
	val totalPage: Int? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("page")
	val page: Int? = null
)

data class ProductOption(

	@field:SerializedName("name")
	val name: String? = null
)

data class CartOptionalProductsItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("product_option")
	val productOption: ProductOption? = null,

	@field:SerializedName("optionlist_carts")
	val optionlistCarts: List<OptionlistCartsItemNewOrder?>? = null
)

data class ProductOptionListNewOrder(

	@field:SerializedName("addtional_price")
	val addtionalPrice: Int? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class AddressUserNewOrder(

	@field:SerializedName("address")
	val address: String? = null
)

data class ProductNewOrder(

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

