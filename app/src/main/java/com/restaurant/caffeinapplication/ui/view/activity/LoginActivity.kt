package com.restaurant.caffeinapplication.ui.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.restaurant.caffeinapplication.databinding.ActivityLoginBinding
import com.restaurant.caffeinapplication.ui.viewModel.LoginViewModel
import com.restaurant.caffeinapplication.utils.Constant
import com.restaurant.caffeinapplication.utils.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    lateinit var binding : ActivityLoginBinding
    lateinit var loginViewModel : LoginViewModel
    lateinit var sharedPref : SharedPreferences
    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        sharedPref = SharedPreferences(this)



        binding.btnLogin.setOnClickListener {
            checkedData()
        }

        loginViewModel.dataLogin.observe(this@LoginActivity){
            Log.d("LOGIN-FAILED", "onCreate: ${it.message}")
            when(it.message){
                "SUCCESS" -> {
                    startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                    Constant.TOKEN_USER = it.token!!
                    sharedPref.putStringData(Constant.NAME_COMPANY,it.data!!.name)
                    sharedPref.putStringData(Constant.ADDRESS_COMPANY,it.data!!.address)
                    var isStoreOpen = false
                    isStoreOpen = it.data?.isOpen == 2
                    sharedPref.putBooleanData(Constant.IS_OPEN,isStoreOpen)
                    binding.progressbar.visibility = View.GONE
                    finish()
                }
                "Password Wrong!" -> {
                    Toast.makeText(this@LoginActivity, "비밀번호를 다시 입력해주세요", Toast.LENGTH_SHORT).show()
                    binding.progressbar.visibility = View.GONE
                }
                "account_id dosnt exist!" -> {
                    Toast.makeText(this@LoginActivity, "계정이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
                    binding.progressbar.visibility = View.GONE
                }
            }
        }

    }

    private fun login(accountId : String, accountPwd : String,fcmToken: String){
        viewModelScope.launch {
            Log.d("fcm-token-adi", "login: $fcmToken")
            loginViewModel.login(accountId,accountPwd,fcmToken)
        }
    }

    private fun checkedData() {
        val accountId = binding.edtUsername.text.toString()
        val accountPwd = binding.edtPassword.text.toString()
        val tokenFcm = sharedPref.getStringData(Constant.NOTIF_FCM)

        if (accountId.isEmpty()) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (accountPwd.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressbar.visibility = View.VISIBLE
        Log.d("PAYLOAD-LOGIN", "checkedData: $tokenFcm")
        login(accountId, accountPwd,tokenFcm!!)
    }
}