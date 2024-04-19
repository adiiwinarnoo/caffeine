package com.restaurant.caffeinapplication.data.model

data class ChildItem(val date: String, val data: List<DataItemVoucher>?)
data class ChildItemIncomeOrder(val date: String, val data: List<DataOrderItem>?)
data class ChildItemIncomeCoupon(val date: String, val data: List<DataCouponItem>?)
data class ChildItemIncomeSubscribe(val date: String, val data: List<DataSubscriptionItem>?)

