package com.restaurant.caffeinapplication.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.caffeinapplication.data.model.ResponseQR
import com.restaurant.caffeinapplication.data.model.ResponseQrScaner
import com.restaurant.caffeinapplication.data.repository.QrScanRepository

class QrScanViewModel : ViewModel() {

    var qrRepo = QrScanRepository()
    var dataQrModel = MutableLiveData<ResponseQrScaner>()
    var qrScannerModel = MutableLiveData<ResponseQR>()

    fun qrScanPreview(codeQr:String) : MutableLiveData<ResponseQrScaner>{
        qrRepo.qrScaner(codeQr){
            dataQrModel.postValue(it)
        }
        return dataQrModel
    }

    fun qrScannerCode(codeQr: String,totalAmount: Int? = null) : MutableLiveData<ResponseQR>{
        qrRepo.qrScannerCode(codeQr = codeQr, totalAmount = totalAmount){
            qrScannerModel.postValue(it)
        }
        return qrScannerModel
    }


}