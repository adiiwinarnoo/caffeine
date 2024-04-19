package com.restaurant.caffeinapplication.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.caffeinapplication.data.model.ResponseLogin
import com.restaurant.caffeinapplication.data.repository.LoginRepository

class LoginViewModel : ViewModel() {

    var loginRepository = LoginRepository()
    var dataLogin = MutableLiveData<ResponseLogin>()

    fun login(accountId : String, accountPwd : String,fcmToken : String) : MutableLiveData<ResponseLogin>{
        loginRepository.login(accountId,accountPwd,fcmToken){
            dataLogin.postValue(it)
        }
        return dataLogin
    }

}