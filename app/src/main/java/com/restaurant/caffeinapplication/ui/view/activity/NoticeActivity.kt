package com.restaurant.caffeinapplication.ui.view.activity

import Popup
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.adapter.NoticeAdapter
import com.restaurant.caffeinapplication.data.network.FirebaseMessageService
import com.restaurant.caffeinapplication.databinding.ActivityNoticeBinding
import com.restaurant.caffeinapplication.ui.viewModel.NoticeViewModel
import com.restaurant.caffeinapplication.ui.viewModel.QrScanViewModel
import com.restaurant.caffeinapplication.ui.viewModel.SettingViewModel
import com.restaurant.caffeinapplication.utils.Constant
import com.restaurant.caffeinapplication.utils.ScanContract
import com.restaurant.caffeinapplication.utils.SharedPreferences
import com.google.zxing.client.android.Intents
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NoticeActivity : AppCompatActivity() ,Popup.PopupCallback {
    private lateinit var binding: ActivityNoticeBinding
    private lateinit var noticeAdapter: NoticeAdapter
    lateinit var viewModelNotice : NoticeViewModel

    var type = "collection"
    var type_data = "company"
    var popup = Popup()
    lateinit var sharedPref : SharedPreferences
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var qrScanViewModel : QrScanViewModel

    companion object {
        var isCouponOrVoucherClaimedShown = false
        var isToastShown = false
    }

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            val originalIntent = result.originalIntent
            if (originalIntent == null) {
            } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                Toast.makeText(this, "카메라 접근 권한이 필요 합니다.\n" +
                        "설정에서 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            }
        } else {
            qrScanner(result.contents)
            Log.d("QR-SCANNNN", ": ${result.contents}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelNotice = ViewModelProvider(this)[NoticeViewModel::class.java]
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        qrScanViewModel = ViewModelProvider(this)[QrScanViewModel::class.java]
        sharedPref = SharedPreferences(this)

        var sideMenu = findViewById<ConstraintLayout>(R.id.btn_side_menu)
        sideMenu.setOnClickListener {
            popup.sideMenuPopup(this,this,this,settingViewModel,
                sharedPref.getBooleanData(Constant.IS_OPEN),barcodeLauncher)
        }

        getNotice(type,type_data)

        viewModelNotice.dataNotice.observe(this){
            when (it.message){
                "SUCCESS" -> {
                    noticeAdapter = NoticeAdapter(it.data!!.result)
                    binding.rvNotice.apply {
                        setHasFixedSize(true)
                        layoutManager = LinearLayoutManager(this@NoticeActivity)
                        adapter = noticeAdapter
                    }

                    noticeAdapter.setItemClickListener(object : NoticeAdapter.ItemClickListener{
                        override fun onClick(view: View, position: Int) {
                            Toast.makeText(this@NoticeActivity, "$position", Toast.LENGTH_SHORT).show()
                        }

                    })

                    for (i in it.data.result!!.indices){
                        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                        val instant = Instant.parse(it.data.result[i]!!.createdAt)
                        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        val dateTime = formatter.format(localDateTime)
                        it.data.result[i]!!.dateString = dateTime
                    }
                }
                "Token Times Up" -> {
                    Toast.makeText(this, "Token Times Up, please login first", Toast.LENGTH_LONG).show()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access, please login first!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        qrScanViewModel.dataQrModel.observe(this){
            var resultItem = it.messageData?.data
            if (it.messageData!!.type.equals("subcriptions",ignoreCase = true)){
                isToastShown = false
                popup.notifPopupScanSubscribe(this,it.messageData.data?.name!!,it.messageData.codeQr!!,
                    qrScanViewModel)
            }else if (it.messageData.type.equals("coupon",ignoreCase = true)){
                isToastShown = false
                popup.notifPopupScanCoupon(this,resultItem?.product?.productName!!,
                    resultItem.product?.thumbnailProduct!!,resultItem.product?.priceProduct!!,
                    it.messageData.codeQr!!,qrScanViewModel)
            }else if (it.messageData.type.equals("voucher",ignoreCase = true)){
                isToastShown = false
                popup.notifPopupScanVoucher(this,resultItem?.voucher?.name!!,resultItem.voucher?.imageVoucher!!,
                    resultItem.amountPrice!!,it.messageData.codeQr!!,qrScanViewModel,this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseMessageService.AppStatus.appIsOpen = true
        qrScanViewModel.qrScannerModel.observe(this){
            Log.d("PESAN-QR", "onResume-SCAN-CODE: ${it.message}")
            Log.d("PESAN-QR", "onResume-coupon: ${HomeActivity.isCouponOrVoucherClaimedShown}")
            when (it.message){
                "SUCCESS" -> {
                    getNotice(type,type_data)
                }
                "Coupon or Voucher Already Claimed" ->{
                    if (isCouponOrVoucherClaimedShown && !isToastShown){
                        Toast.makeText(this, "구독권, 기프티콘이 이미 사용되었습니다", Toast.LENGTH_SHORT).show()
                        isToastShown = true
                    }
                }
                "Total Amount Cannot Be More Than Voucher Amount" -> {
                    if (isCouponOrVoucherClaimedShown && !isToastShown) {
                        Toast.makeText(this, "Total Amount Cannot Be More Than Voucher Amount",
                            Toast.LENGTH_SHORT).show()
                        isToastShown = true
                    }
                }
            }
        }
        settingViewModel.dataUpdateStatus.observe(this){
            Log.d("UPDATE-STATUS", "onResume: ${it.message}")
            when (it.message){
                "SUCCESS" -> {
                    Log.d("UPDATE-STATUS", "onResume: ${it.data?.isOpen}")
                    var isStoreOpen = false
                    isStoreOpen = it.data?.isOpen == 2
                    sharedPref.putBooleanData(Constant.IS_OPEN,isStoreOpen)
                    popup.updateStoreStatus(isStoreOpen)
                }
            }
        }

    }

    private fun getNotice(type : String, typeData : String){
        viewModelNotice.getNotice(type,typeData)
    }

    private fun qrScanner(codeQr:String){
        qrScanViewModel.qrScanPreview(codeQr)
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}