package com.restaurant.caffeinapplication.ui.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.restaurant.caffeinapplication.databinding.ActivitySplashScreenBinding
import com.restaurant.caffeinapplication.ui.viewModel.ProductStockViewModel
import com.restaurant.caffeinapplication.utils.Constant
import com.restaurant.caffeinapplication.utils.SharedPreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashScreenActivity : AppCompatActivity() {

    lateinit var binding : ActivitySplashScreenBinding
    lateinit var viewModelProduct : ProductStockViewModel
    private val viewModelScope = CoroutineScope(Dispatchers.IO)
    private var tokenFcm : String? = null
    lateinit var sharedPref : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelProduct = ViewModelProvider(this).get(ProductStockViewModel::class.java)
        sharedPref = SharedPreferences(this)

        //setup firebase
        FirebaseApp.initializeApp(this)
        getTokenFCM()

        viewModelScope.launch {
            checked()
        }
    }

    private suspend fun checked(){
        getProduct()
        withContext(Dispatchers.Main){
            viewModelProduct.dataProductStock.observe(this@SplashScreenActivity){
                when (it.message) {
                    "SUCCESS" -> {
                        startActivity(Intent(this@SplashScreenActivity,HomeActivity::class.java))
                        finish()
                    }
                    "Token Times Up" -> {
                        startActivity(Intent(this@SplashScreenActivity,LoginActivity::class.java))
                        finish()
                    }
                    "you dont have access!" ->{
                        Toast.makeText(this@SplashScreenActivity, "you dont have access, please login first!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun getProduct(){
        viewModelProduct.getProductStocks()
    }

    private fun getTokenFCM(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("fcm-add", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            tokenFcm = task.result
            sharedPref.putStringData(Constant.NOTIF_FCM,tokenFcm)

            // Log and toast
            println("TOKEN-FCM" + tokenFcm)
            Log.d("INI-TOKEN", "getTokenFCM: $tokenFcm")
        })
    }
}