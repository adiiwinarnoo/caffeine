package com.restaurant.caffeinapplication.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.caffeinapplication.data.model.ResponseNotice
import com.restaurant.caffeinapplication.data.repository.NoticeRepository

class NoticeViewModel : ViewModel() {

    var repoNotice = NoticeRepository()
    var dataNotice = MutableLiveData<ResponseNotice>()

    fun getNotice(type : String, typeData : String): MutableLiveData<ResponseNotice>{
        repoNotice.getNotice(type,typeData){
            dataNotice.postValue(it)
        }
        return dataNotice
    }

}