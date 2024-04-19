package com.restaurant.caffeinapplication.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.caffeinapplication.data.model.ResponseSettingStatusStore
import com.restaurant.caffeinapplication.data.repository.SettingRepository

class SettingViewModel : ViewModel() {

    var settingRepo = SettingRepository()
    var dataUpdateStatus = MutableLiveData<ResponseSettingStatusStore>()

    fun sendUpdateStatus(isOpen : Int) : MutableLiveData<ResponseSettingStatusStore>{
        settingRepo.updateStatusStore(isOpen = isOpen){
            dataUpdateStatus.postValue(it)
        }
        return dataUpdateStatus
    }

}