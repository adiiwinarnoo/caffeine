package com.restaurant.caffeinapplication.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.caffeinapplication.data.model.ResponseNewOrder
import com.restaurant.caffeinapplication.data.repository.HomeRepository

class ProductHomeViewModel : ViewModel() {

    var repoProductHome = HomeRepository()
    var productHomeData = MutableLiveData<ResponseNewOrder>()
    var productHomeDataByFilter = MutableLiveData<ResponseNewOrder>()

    fun getProductHome() : MutableLiveData<ResponseNewOrder>{
        repoProductHome.getProduct {
            productHomeData.postValue(it)
        }
        return productHomeData
    }

    fun getProductByStatus(status : String,type : String?=null,orderBy : String?=null,orderType : String?=null) : MutableLiveData<ResponseNewOrder>{
        repoProductHome.getProductByStatus(status,type,orderBy,orderType){
            productHomeData.postValue(it)
        }
        return productHomeData
    }

    fun getProductByFilter(starDate : String? = null, endDate : String? = null,typeOrder : String? = null,
                           orderFrom : String? = null, name : String? = null,status : String? = null,
                           type : String?=null,orderBy : String?=null,orderType : String?=null) : MutableLiveData<ResponseNewOrder>{
        repoProductHome.getProductByFilterOrder(starDate = starDate, endDate = endDate, typeOrder = typeOrder, orderFrom = orderFrom,
            name = name, orderType = orderType){
            productHomeDataByFilter.postValue(it)
        }
        return productHomeDataByFilter
    }

}