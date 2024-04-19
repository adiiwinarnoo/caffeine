package com.restaurant.caffeinapplication.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.caffeinapplication.data.model.ResponseUpdateOrder
import com.restaurant.caffeinapplication.data.repository.ProductEstimateRepository

class ProductEstimateViewModel : ViewModel() {

    var repoProductEstimate = ProductEstimateRepository()
    var dataProductEstimate = MutableLiveData<ResponseUpdateOrder>()
    var dataUpdatePickup = MutableLiveData<ResponseUpdateOrder>()
    var dataRejected = MutableLiveData<ResponseUpdateOrder>()

    fun updateEstimate(idProduct : Int, status : String, estimate : Int? = null) : MutableLiveData<ResponseUpdateOrder>{
        if (estimate != null){
            repoProductEstimate.updateProductEstimate(idProduct,status,estimate){
                dataProductEstimate.postValue(it)
            }
        }
        return dataProductEstimate
    }

    fun updatePickup(idProduct: Int,status: String) : MutableLiveData<ResponseUpdateOrder>{
        repoProductEstimate.updateProductEstimate(idProduct,status){
            dataUpdatePickup.postValue(it)
        }
        return dataUpdatePickup
    }

    fun rejectedPickup(idProduct: Int,status: String,descriptionRejected : String) : MutableLiveData<ResponseUpdateOrder>{
        repoProductEstimate.updateProductEstimate(idProduct,status, descriptionReject = descriptionRejected){
            dataRejected.postValue(it)
        }
        return dataRejected
    }


}