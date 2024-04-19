package com.restaurant.caffeinapplication.data.model

import com.google.gson.annotations.SerializedName

data class ResponseIncomeOrder(

	@field:SerializedName("response")
	val response: List<ResponseItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class CartIncomeOrder(

	@field:SerializedName("product")
	val product: ProductIncomeOrder? = null,

	@field:SerializedName("company")
	val company: CompanyIncomeOrder? = null,

	@field:SerializedName("product_and_option_total_price")
	val productAndOptionTotalPrice: Int? = null,

	@field:SerializedName("cart_optional_products")
	val cartOptionalProducts: List<CartOptionalProductsItemIncomeOrder?>? = null
)

data class ChartOrdersItemIncomeOrder(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("orderId")
	val orderId: Int? = null,

	@field:SerializedName("cartId")
	val cartId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("cart")
	val cart: CartIncomeOrder? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class DataOrderItem(

	@field:SerializedName("total_gross")
	val totalGross: Int? = null,

	@field:SerializedName("total_price")
	val totalPrice: Int? = null,

	@field:SerializedName("chart_orders")
	val chartOrders: List<ChartOrdersItemIncomeOrder?>? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("status_payment")
	val statusPayment: Boolean? = null,

	@field:SerializedName("order_voucher_histories")
	val orderVoucherHistories: List<OrderVoucherHistoriesItem?>? = null,

	@field:SerializedName("address_user")
	val addressUser: AddressUserIncomeOrder? = null,

	@field:SerializedName("payment")
	val payment: PaymentIncomeOrder? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("order_type")
	val orderType: String? = null,

	@field:SerializedName("user")
	val user: UserIncomeOrder? = null,

	@field:SerializedName("delivery_price")
	val deliveryPrice: Int? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("estimated_time")
	val estimatedTime: Int? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class UserIncomeOrder(

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class OrderVoucherHistoriesItem(

	@field:SerializedName("amount")
	val amount: Int? = null
)

data class ProductOptionListIncomeOrder(

	@field:SerializedName("addtional_price")
	val addtionalPrice: Int? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class OptionlistCartsItemIncomeOrder(

	@field:SerializedName("product_option_list")
	val productOptionList: ProductOptionListIncomeOrder? = null,

	@field:SerializedName("value")
	val value: Int? = null
)

data class CartOptionalProductsItemIncomeOrder(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("product_option")
	val productOption: ProductOptionIncomeOrder? = null,

	@field:SerializedName("optionlist_carts")
	val optionlistCarts: List<OptionlistCartsItemIncomeOrder?>? = null
)

data class MonthItemIncomeOrder(

	@field:SerializedName("date")
	val date: List<DateItemIncomeOrder?>? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("month")
	val month: Int? = null
)

data class ProductIncomeOrder(

	@field:SerializedName("thumbnail")
	val thumbnail: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class ProductOptionIncomeOrder(

	@field:SerializedName("name")
	val name: String? = null
)

data class DateItemIncomeOrder(

	@field:SerializedName("date")
	val date: Int? = null,

	@field:SerializedName("total_income")
	val totalIncome: Int? = null,

	@field:SerializedName("data_order")
	val dataOrder: List<DataOrderItem?>? = null
)

data class AddressUserIncomeOrder(

	@field:SerializedName("address")
	val address: String? = null
)

data class PaymentIncomeOrder(

	@field:SerializedName("payment_type")
	val paymentType: String? = null,

	@field:SerializedName("third_party_id")
	val thirdPartyId: String? = null,

	@field:SerializedName("transaction")
	val transaction: String? = null
)

data class CompanyIncomeOrder(

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class ResponseItem(

	@field:SerializedName("month")
	val month: List<MonthItemIncomeOrder?>? = null,

	@field:SerializedName("year")
	val year: Int? = null
)
