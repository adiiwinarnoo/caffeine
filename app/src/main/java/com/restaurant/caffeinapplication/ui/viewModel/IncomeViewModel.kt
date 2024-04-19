package com.restaurant.caffeinapplication.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.caffeinapplication.data.model.ResponseIncomeCoupon
import com.restaurant.caffeinapplication.data.model.ResponseIncomeNew
import com.restaurant.caffeinapplication.data.model.ResponseIncomeOrder
import com.restaurant.caffeinapplication.data.model.ResponseIncomeSubscribe
import com.restaurant.caffeinapplication.data.repository.IncomeRepository

class IncomeViewModel : ViewModel() {

    var incomeRepository = IncomeRepository()
    var dataIncome = MutableLiveData<ResponseIncomeNew>()
    var dataIncomeOrder = MutableLiveData<ResponseIncomeOrder>()
    var dataIncomeCoupon = MutableLiveData<ResponseIncomeCoupon>()
    var dataIncomeSubscribe = MutableLiveData<ResponseIncomeSubscribe>()

    fun getAllVoucherViewModel(starDate : String? = null, endDate : String? = null) : MutableLiveData<ResponseIncomeNew>{
        incomeRepository.getAllVoucher(starDate = starDate, endDate = endDate) {
            dataIncome.postValue(it)
        }
        return dataIncome
    }

    fun getHistoryIncomeOrder(starDate : String? = null, endDate : String? = null,typeOrder: String? = null) :
            MutableLiveData<ResponseIncomeOrder>{
        incomeRepository.getOrderHistory(starDate = starDate, endDate = endDate, typeOrder = typeOrder) {
            dataIncomeOrder.postValue(it)
        }
        return dataIncomeOrder
    }
    fun getAllCouponViewModel(starDate : String? = null, endDate : String? = null,typeOrder: String? = null) :
            MutableLiveData<ResponseIncomeCoupon>{
        incomeRepository.getAllCoupon(starDate = starDate, endDate = endDate) {
            dataIncomeCoupon.postValue(it)
        }
        return dataIncomeCoupon
    }
    fun getAllSubscribeViewModel(starDate : String? = null, endDate : String? = null) :
            MutableLiveData<ResponseIncomeSubscribe>{
        incomeRepository.getAllSubscribe(starDate = starDate, endDate = endDate) {
            dataIncomeSubscribe.postValue(it)
        }
        return dataIncomeSubscribe
    }

}