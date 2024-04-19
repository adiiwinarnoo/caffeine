package com.restaurant.caffeinapplication.ui.view.activity

import Popup
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.adapter.*
import com.restaurant.caffeinapplication.data.model.ResultItemNewOrder
import com.restaurant.caffeinapplication.data.network.FirebaseMessageService
import com.restaurant.caffeinapplication.databinding.ActivityHomeBinding
import com.restaurant.caffeinapplication.ui.viewModel.ProductEstimateViewModel
import com.restaurant.caffeinapplication.ui.viewModel.ProductHomeViewModel
import com.restaurant.caffeinapplication.ui.viewModel.QrScanViewModel
import com.restaurant.caffeinapplication.ui.viewModel.SettingViewModel
import com.restaurant.caffeinapplication.utils.Constant
import com.restaurant.caffeinapplication.utils.ScanContract
import com.restaurant.caffeinapplication.utils.ScanOptions
import com.restaurant.caffeinapplication.utils.SharedPreferences
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.zxing.client.android.Intents
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeActivity : AppCompatActivity(),Popup.PopupCallback {

    lateinit var binding : ActivityHomeBinding
    lateinit var adapter : ProductNewOrderAdapter
    lateinit var adapterOngoing : ProductOngoingAdapter
    lateinit var adapterComplete : ProductCompleteOrderAdapter
    lateinit var adapterRejected : ProductRejectedOrderAdapter
    lateinit var viewModelHome : ProductHomeViewModel
    lateinit var adapterProductName : ProductNamePopupAdapter
    lateinit var adapterVoucher : VoucherPopupAdapter
    var popup = Popup()
    lateinit var sharedPref : SharedPreferences
    private var selectedEstimateTime: Int = 0
    private lateinit var productEstimateViewModel : ProductEstimateViewModel
    private lateinit var qrScanViewModel : QrScanViewModel
    private lateinit var settingViewModel: SettingViewModel
    val orderedResults = mutableListOf<ResultItemNewOrder?>()
    val ongoingResult = mutableListOf<ResultItemNewOrder?>()
    val completeResult = mutableListOf<ResultItemNewOrder?>()
    val rejectedResult = mutableListOf<ResultItemNewOrder?>()

    companion object {
        lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>
        var isCouponOrVoucherClaimedShown = false
        var isToastShown = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelHome = ViewModelProvider(this)[ProductHomeViewModel::class.java]
        productEstimateViewModel = ViewModelProvider(this).get(ProductEstimateViewModel::class.java)
        var sideMenu = findViewById<ConstraintLayout>(R.id.btn_side_menu)
        sharedPref = SharedPreferences(this)

        binding.progressbar.visibility = View.VISIBLE
        binding.tvNewOrder.visibility = View.GONE
        binding.tvProgressOrder.visibility = View.GONE
        binding.tvCompleteOrder.visibility = View.GONE
        binding.tvRejectedOrder.visibility = View.GONE
        FirebaseMessageService.AppStatus.appIsOpen = true


        barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
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

        binding.allOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
        binding.allOrder.setTextColor(Color.WHITE)

        getProductByType(orderType = "desc")
        filterButton()

        qrScanViewModel = ViewModelProvider(this)[QrScanViewModel::class.java]
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = false
            completeResult.clear()
            getProductByType(orderType = "desc")
        }

        binding.btnScan.setOnClickListener {
            val options: ScanOptions = ScanOptions().setOrientationLocked(false).setCaptureActivity(
                ScanQrActivity::class.java
            )
            barcodeLauncher.launch(options)
        }
        sideMenu.setOnClickListener {
//            sideMenuPopup()
            popup.sideMenuPopup(this,this,this,settingViewModel,
                sharedPref.getBooleanData(Constant.IS_OPEN), barcodeLauncher)
        }

        binding.recyclerNewOrder.layoutManager = LinearLayoutManager(this)
        binding.recyclerProgressOrder.layoutManager = LinearLayoutManager(this)
        binding.recyclerCompleteOrder.layoutManager = LinearLayoutManager(this)
        binding.recyclerRejectOrder.layoutManager = LinearLayoutManager(this)

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
        productEstimateViewModel.dataUpdatePickup.observe(this){
            Log.d("HIRR", "onCreate: ${it.message}")
            when(it.message){
                "SUCCESS" -> {
                    orderedResults.clear()
                    ongoingResult.clear()
                    binding.progressbar.visibility = View.GONE
                    getProductByType(orderType = "desc")
                }
                "Token Times Up" -> {
                    Toast.makeText(this, "Token Times Up, please login first",
                        Toast.LENGTH_LONG).show()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access, please login first!",
                        Toast.LENGTH_SHORT).show()
                }
            }

        }

        viewModelHome.productHomeDataByFilter.observe(this){
            Log.d("HIRR", "onCreate222: ${it.message}")
            when (it.message){
                "SUCCESS" -> {
                    orderedResults.clear()
                    ongoingResult.clear()
                    binding.progressbar.visibility = View.GONE
                    for (item in it.data!!.result!!) {
                        if (item!!.status.equals("ordered", ignoreCase = true)) {
                            orderedResults.add(item)
                        }else if (item.status.equals("making") || item.status.equals("delivery")
                            || item.status.equals("pickup") || item.status.equals("waiting_user_pickup")) {
                            ongoingResult.add(item)
                        }else if(item.status.equals("finished")) {
                            completeResult.add(item)
                        }else if (item.status.equals("rejected")){
                            rejectedResult.add(item)
                        }
                    }
                    adapter = ProductNewOrderAdapter(orderedResults)
                    adapterOngoing = ProductOngoingAdapter(ongoingResult)
                    adapterComplete = ProductCompleteOrderAdapter(completeResult)
                    adapterRejected = ProductRejectedOrderAdapter(rejectedResult)

                    binding.tvNewOrder.visibility = View.VISIBLE
                    binding.tvProgressOrder.visibility = View.VISIBLE
                    binding.tvCompleteOrder.visibility = View.VISIBLE
                    binding.tvRejectedOrder.visibility = View.VISIBLE
                    binding.recyclerNewOrder.visibility = View.VISIBLE
                    binding.recyclerProgressOrder.visibility = View.VISIBLE
                    binding.recyclerCompleteOrder.visibility = View.VISIBLE
                    binding.recyclerRejectOrder.visibility = View.VISIBLE

                    binding.recyclerNewOrder.adapter = adapter
                    binding.recyclerProgressOrder.adapter = adapterOngoing
                    binding.recyclerCompleteOrder.adapter = adapterComplete
                    binding.recyclerRejectOrder.adapter = adapterRejected

                    // Tetapkan ItemClickListener seperti yang Anda lakukan sebelumnya
                    adapter.setItemClickListener(object : ProductNewOrderAdapter.ItemClickListener {
                        override fun onClick(view: View, position: Int,idProduct: Int) {
                            reasonCancel(this@HomeActivity,idProduct)
                        }
                    })

                    adapter.setItemClickListener(object : ProductNewOrderAdapter.ItemClickListener2 {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClick(view: View, position: Int, customerName: String,
                                             addressUser: String, productName: String, priceProduct: Int,
                                             orderNumber: Int, dateTime : String, deliveryCost : Int,
                                             totalPayment : Int, idProduct : Int,models: List<ResultItemNewOrder?>?
                                             ,type: String,paymentMethod: String,totalGross:Int) {
                            if (!type.equals("pickup") || !type.equals("dinein")){
                                confirmNewOrder(this@HomeActivity,position,customerName,addressUser,productName,
                                    priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                    sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,
                                    dateTime,deliveryCost,totalPayment, idProduct,models,totalGross,paymentMethod)
                            }else{
                                Log.d("DEBUG-IDA", "onClick: ")
                                pickuPopup(this@HomeActivity,position,customerName,addressUser,productName,
                                    priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                    sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,
                                    dateTime,deliveryCost,totalPayment, idProduct,paymentMethod,models,totalGross)
                            }
                        }
                    })
                    adapterOngoing.setItemClickDetail(object : ProductOngoingAdapter.ItemClickDetailProduct{
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClick(view: View, position: Int, customerName: String,
                                             addressUser: String, productName: String, priceProduct: Int,
                                             orderNumber: Int, dateTime: String, deliveryCost: Int, totalPayment: Int,
                                             idProduct: Int, models: List<ResultItemNewOrder?>?, type: String,
                                             paymentMethod: String, totalGross: Int, typeOrder: String, userPhone: String) {
                            detailProduct(this@HomeActivity,position,customerName,addressUser,productName,
                                priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,
                                dateTime,deliveryCost,totalPayment, idProduct,models,totalGross,paymentMethod,typeOrder,userPhone)
                        }


                    });
                    adapterComplete.setItemClickDetail(object : ProductCompleteOrderAdapter.ItemClickDetailProduct{
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClick(view: View, position: Int, customerName: String,
                                             addressUser: String, productName: String, priceProduct: Int,
                                             orderNumber: Int, dateTime: String, deliveryCost: Int, totalPayment: Int,
                                             idProduct: Int, models: List<ResultItemNewOrder?>?, type: String,
                                             paymentMethod: String, totalGross: Int, typeOrder: String, userPhone: String) {
                            detailProduct(this@HomeActivity,position,customerName,addressUser,productName,
                                priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,
                                dateTime,deliveryCost,totalPayment, idProduct,models,totalGross,paymentMethod,typeOrder,userPhone)
                        }
                    });
                    adapterOngoing.setItemClickListener(object : ProductOngoingAdapter.ItemClickListener{
                        override fun onClick(view: View, position: Int, idProduct: Int,type : String) {
                            Log.d("HIRR-TYPE", "onClick: $type,$idProduct")
                            productEstimateViewModel.updatePickup(idProduct,"waiting_user_pickup")
                        }
                    })
                    adapterOngoing.setItemClickListener(object : ProductOngoingAdapter.ItemClickListenerFinish{
                        override fun onClick(view: View, position: Int, idProduct: Int, type: String) {
                            productEstimateViewModel.updatePickup(idProduct,"finished")
                        }

                    })
                    adapterOngoing.setItemClickListener(object : ProductOngoingAdapter.ItemClickListener3{
                        override fun onClickPickup(view: View, position: Int, idProduct: Int) {
                            popupConfirmDelivery(this@HomeActivity, idProduct,"pickup")
                        }

                        override fun pickupOnly(view: View, position: Int, idProduct: Int, type: String) {
                            popupConfirmDelivery(this@HomeActivity, idProduct,"delivery", type = type)
                        }
                    })
                    adapterOngoing.setItemClickListener(object : ProductOngoingAdapter.ItemClickListener2{
                        override fun onClickDelivery(view: View, position: Int, idProduct: Int) {
                            popupConfirmDelivery(this@HomeActivity, idProduct,"delivery")
                        }
                    })
                    adapterComplete.setItemClickListener(object : ProductCompleteOrderAdapter.ItemClickListener{
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClickDeliverOrder(view: View, position: Int, customerName: String,
                                                         addressUser: String, productName: String,
                                                         priceProduct: Int, orderNumber: Int,
                                                         dateTime: String, deliveryCost: Int, totalPayment: Int,
                                                         models : List<ResultItemNewOrder?>?,totalGross: Int) {
                            popup.deliveryOrderDetails(this@HomeActivity,position,customerName,
                                addressUser,productName, priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,dateTime,
                                deliveryCost,totalPayment,models,totalGross)
                        }
                    })
                    adapterComplete.setItemClickListener(object : ProductCompleteOrderAdapter.ItemClickListener2{
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClickPackagingOrder(view: View, position: Int, customerName: String,
                                                           productName: String,
                                                           priceProduct: Int, orderNumber: Int,
                                                           dateTime: String, deliveryCost: Int, totalPayment: Int,
                                                           paymentMethod : String,models : List<ResultItemNewOrder?>?,
                                                           totalGross: Int) {
                            popup.packagingOrderDetails(this@HomeActivity,position,customerName,
                                productName, priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,dateTime,
                                deliveryCost,totalPayment,paymentMethod,models,totalGross)
                        }

                    })
                    adapterRejected.setItemClickListener(object : ProductRejectedOrderAdapter.ItemClickListener{
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClickDeliverOrder(view: View, position: Int, customerName: String,
                                                         addressUser: String, productName: String,
                                                         priceProduct: Int, orderNumber: Int,
                                                         dateTime: String, deliveryCost: Int, totalPayment: Int,
                                                         models : List<ResultItemNewOrder?>?,totalGross: Int) {
                            popup.deliveryOrderDetails(this@HomeActivity,position,customerName,
                                addressUser,productName,
                                priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,dateTime,
                                deliveryCost,totalPayment,models,totalGross)
                        }

                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClickPackagingOrder(view: View, position: Int, customerName: String,
                                                           addressUser: String?, productName: String,
                                                           priceProduct: Int, orderNumber: Int,
                                                           dateTime: String, deliveryCost: Int,
                                                           totalPayment: Int, paymentMethod: String,
                                                           models : List<ResultItemNewOrder?>?,totalGross: Int) {
                            popup.packagingOrderDetails(this@HomeActivity,position,customerName,
                                productName,
                                priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,dateTime,
                                deliveryCost,totalPayment,paymentMethod,models,totalGross)
                        }
                    })
                }
                "Token Times Up" -> {
                    Constant.TOKEN_USER = null.toString()
                    startActivity(Intent(this,LoginActivity::class.java))
                    Toast.makeText(this, "token times up", Toast.LENGTH_SHORT).show()
                    finish()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access, please login first!",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseMessageService.AppStatus.appIsOpen = true
        qrScanViewModel.qrScannerModel.observe(this){
            Log.d("PESAN-QR", "onResume-SCAN-CODE: ${it.message}")
            Log.d("PESAN-QR", "onResume-coupon: ${isCouponOrVoucherClaimedShown}")
            when (it.message){
                "SUCCESS" -> {
                    getProductByType(orderType = "desc")
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

    private fun getProductByType(orderType: String?= null, typeOrder : String?= null){
        viewModelHome.getProductByFilter(orderType = orderType, typeOrder = typeOrder)
    }
    fun qrScanner(codeQr:String){
        qrScanViewModel.qrScanPreview(codeQr)
    }

    fun popupConfirmDelivery( context: Context,idProduct: Int,status : String? = null, type : String?=null ) {
        productEstimateViewModel = ViewModelProvider(this).get(ProductEstimateViewModel::class.java)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_confirm_pickup, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonSubmit = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val titleName = menuPopup.findViewById<TextView>(R.id.tvTittlePopup)
        val imageOut = menuPopup.findViewById<ConstraintLayout>(R.id.conts_1)

        imageOut.setOnClickListener {
            popup.dismiss()
        }
        imgClose.setOnClickListener {
            popup.dismiss()
        }
        when(status){
            "making"-> {
                if (type.equals("pickup",ignoreCase = true) || type.equals("dinein", ignoreCase = true)){
                    titleName.text = "고객에게 알리겠습니까?"
                    buttonSubmit.setOnClickListener {
                        binding.progressbar.visibility = View.VISIBLE
                        productEstimateViewModel.updatePickup(idProduct,"pickup")
                    }
                }else{
                    titleName.text = "제조 완료하였나요?"
                    buttonSubmit.setOnClickListener {
                        binding.progressbar.visibility = View.VISIBLE
                        productEstimateViewModel.updatePickup(idProduct,"pickup")
                    }
                }
            }
            "pickup"-> {
                    titleName.text = "배송기사 픽업대기."
                    buttonSubmit.setOnClickListener {
                        binding.progressbar.visibility = View.VISIBLE
                        productEstimateViewModel.updatePickup(idProduct,"delivery")
                }
            }
            "delivery" -> {
                if (type.equals("pickup",ignoreCase = true) || type.equals("dinein",ignoreCase = true)) {
                    titleName.text = "정말 주문을 종료하시겠습니까?"
                    buttonSubmit.setOnClickListener {
                        binding.progressbar.visibility = View.VISIBLE
                        productEstimateViewModel.updatePickup(idProduct, "finished")
                    }
                }else{
                        titleName.text = "배송되고 있습니다."
                        buttonSubmit.setOnClickListener {
                            popup.dismiss()
                        }
                    }
            }
        }
        productEstimateViewModel.dataUpdatePickup.observe(this){

            when(it.message){
                "SUCCESS" -> {
                    binding.progressbar.visibility = View.GONE
                    popup.dismiss()
                    getProductByType(orderType = "desc")
                }
                "Token Times Up" -> {
                    Toast.makeText(this, "Token Times Up, please login first",
                        Toast.LENGTH_LONG).show()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access, please login first!",
                        Toast.LENGTH_SHORT).show()
                }
            }

        }

        popup.setBackgroundDrawable(null)
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun confirmNewOrder(context: Context,position : Int,customerName : String, addressUser : String, productName : String,
                        priceProduct : Int, storeName : String, storeAddress : String, orderNumber : Int,
                        orderDateTime : String, deliveryCost : Int, totalPayment : Int, idProduct : Int,
                        models: List<ResultItemNewOrder?>?,totalGross: Int,paymentMethod: String){
        sharedPref = SharedPreferences(context)
        productEstimateViewModel = ViewModelProvider(this).get(ProductEstimateViewModel::class.java)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_call_delivery,null)
        val dialog = BottomSheetDialog(context)
        val tvEstimateTime = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvStoreName = menuPopup.findViewById<TextView>(R.id.tv_store_name)
        val tvStoreAddress = menuPopup.findViewById<TextView>(R.id.tv_store_address)
        val tvOrderNumber = menuPopup.findViewById<TextView>(R.id.tv_order_number)
        val tvOrderDateTime = menuPopup.findViewById<TextView>(R.id.tv_order_date_time)
        val tvCustomerName = menuPopup.findViewById<TextView>(R.id.tv_customer_name)
        val tvDeliverAddress = menuPopup.findViewById<TextView>(R.id.tv_address)
        val tvDeliveryCost = menuPopup.findViewById<TextView>(R.id.value_delivery_cost)
        val tvTotalPrice = menuPopup.findViewById<TextView>(R.id.value_general_payment)
        val tvEstimate = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvConfirm = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val tvTotalGross = menuPopup.findViewById<TextView>(R.id.tv_sum_price)
        val recyclerView = menuPopup.findViewById<RecyclerView>(R.id.recyclerview)
        val tvPaymentMethod2 = menuPopup.findViewById<TextView>(R.id.tv_delivery_cost)
        val tvPaymentMethod = menuPopup.findViewById<TextView>(R.id.tv_general_payment)
        val recyclerViewPrice = menuPopup.findViewById<RecyclerView>(R.id.recyclerview_voucher)

        recyclerViewPrice.layoutManager = LinearLayoutManager(this)
        Log.d("DEBUG-IDA", "onBindViewHolder: ${models?.get(position)?.orderVoucherHistories?.size}")
        adapterVoucher = VoucherPopupAdapter(models!![position])
        recyclerViewPrice.adapter = adapterVoucher


        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterProductName = ProductNamePopupAdapter(models!![position])
        recyclerView.adapter = adapterProductName

        when (paymentMethod) {
            "CARD" -> {
                tvPaymentMethod.text = "카드"
            }
            "kakao" -> {
                tvPaymentMethod.text = "카카오페이"
            }
            "Coupon" -> {
                tvPaymentMethod.text = "쿠폰"
            }
            "Subscription" -> {
                tvPaymentMethod.text = "구독"
            }
        }


        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val instant = Instant.parse(orderDateTime)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dateTime = formatter.format(localDateTime)
        val estimateTime = sharedPref.getIntData(Constant.ESTIMATE_TIME)

        if (Constant.ESTIMATE_TIME.equals("estimateTime")){
            tvEstimate.text = "$estimateTime  분"
        }else{
            tvEstimate.text = "$estimateTime  분"
        }
        if (addressUser != null){
            tvDeliverAddress.text = addressUser
        }

        tvStoreName.text = storeName
        tvStoreAddress.text = storeAddress
        tvOrderNumber.text = orderNumber.toString()
        tvOrderDateTime.text = dateTime
        tvCustomerName.text = customerName
        tvDeliveryCost.text = deliveryCost.toString()
        if (totalPayment != 0) tvTotalPrice.text = totalPayment.toString()
        else tvTotalPrice.visibility = View.GONE
        tvTotalGross.text = totalGross.toString()

        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        dialog.setContentView(menuPopup)
        imgClose.setOnClickListener {
            dialog!!.dismiss()
        }
        dialog.setOnCancelListener {  }
        dialog.show()


        tvEstimateTime.setOnClickListener {
            popupEstimate(context) { estimateTime ->
                val tvEstimate = dialog.findViewById<TextView>(R.id.tv_choice)
                tvEstimate?.text = "$estimateTime  분"
            }
        }
        tvConfirm.setOnClickListener {
            dialog.dismiss()
            productEstimateViewModel.updateEstimate(idProduct,"making",estimateTime)
        }

        productEstimateViewModel.dataProductEstimate.observe(this){
            when(it.message){
                "SUCCESS" -> {
                    getProductByType(orderType = "desc")
                }
                "Token Times Up" -> {
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
                "you dont have access!" ->{
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun detailProduct(context: Context,position : Int,customerName : String, addressUser : String, productName : String,
                        priceProduct : Int, storeName : String, storeAddress : String, orderNumber : Int,
                        orderDateTime : String, deliveryCost : Int, totalPayment : Int, idProduct : Int,
                        models: List<ResultItemNewOrder?>?,totalGross: Int,paymentMethod: String, typeOrder: String?,userPhone: String?){
        sharedPref = SharedPreferences(context)
        productEstimateViewModel = ViewModelProvider(this).get(ProductEstimateViewModel::class.java)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_product_detail,null)
        val dialog = BottomSheetDialog(context)
        val titlePopup = menuPopup.findViewById<TextView>(R.id.tv_popup_tittle)
        val orderId = menuPopup.findViewById<TextView>(R.id.in_order_id)
        val orderDate = menuPopup.findViewById<TextView>(R.id.in_order_date)
        val addressUser2 = menuPopup.findViewById<TextView>(R.id.in_address_user)
        val tvAddressUser = menuPopup.findViewById<TextView>(R.id.tv_store_owner_point)
        val username = menuPopup.findViewById<TextView>(R.id.tv_order_number)
        val phoneUser = menuPopup.findViewById<TextView>(R.id.tv_order_date_time)
        val tvDeliveryCost = menuPopup.findViewById<TextView>(R.id.value_delivery_cost)
        val tvTotalPrice = menuPopup.findViewById<TextView>(R.id.value_general_payment)
        val tvTotalGross = menuPopup.findViewById<TextView>(R.id.tv_sum_price)
        val recyclerView = menuPopup.findViewById<RecyclerView>(R.id.recyclerview)
        val tvPaymentMethod2 = menuPopup.findViewById<TextView>(R.id.tv_delivery_cost)
        val tvPaymentMethod = menuPopup.findViewById<TextView>(R.id.tv_general_payment)
        val recyclerViewPrice = menuPopup.findViewById<RecyclerView>(R.id.recyclerview_voucher)

        recyclerViewPrice.layoutManager = LinearLayoutManager(this)
        Log.d("DEBUG-IDA", "onBindViewHolder: ${models?.get(position)?.orderVoucherHistories?.size}")
        adapterVoucher = VoucherPopupAdapter(models!![position])
        recyclerViewPrice.adapter = adapterVoucher

        if (typeOrder.equals("delivery")){
            titlePopup.text = "주문내역"
            addressUser2.text = addressUser
        }else if (typeOrder.equals("pickup") || typeOrder.equals("dinein")){
            titlePopup.text = "포장 주문내역"
            tvAddressUser.visibility = View.INVISIBLE
            addressUser2.visibility = View.INVISIBLE
        }

        orderId.text = orderNumber.toString()
        username.text = customerName
        phoneUser.text = userPhone


        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterProductName = ProductNamePopupAdapter(models!![position])
        recyclerView.adapter = adapterProductName

        when (paymentMethod) {
            "CARD" -> {
                tvPaymentMethod.text = "카드"
            }
            "kakao" -> {
                tvPaymentMethod.text = "카카오페이"
            }
            "Coupon" -> {
                tvPaymentMethod.text = "쿠폰"
            }
            "Subscription" -> {
                tvPaymentMethod.text = "구독"
            }
        }


        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val instant = Instant.parse(orderDateTime)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dateTime = formatter.format(localDateTime)
        val estimateTime = sharedPref.getIntData(Constant.ESTIMATE_TIME)

        orderDate.text = dateTime
        tvDeliveryCost.text = deliveryCost.toString()
        if (totalPayment != 0) tvTotalPrice.text = totalPayment.toString()
        else tvTotalPrice.visibility = View.GONE
        tvTotalGross.text = totalGross.toString()

        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        dialog.setContentView(menuPopup)
        imgClose.setOnClickListener {
            dialog!!.dismiss()
        }
        dialog.setOnCancelListener {  }
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun pickuPopup(context: Context,position : Int,customerName : String, addressUser : String, productName : String,
                        priceProduct : Int, storeName : String, storeAddress : String, orderNumber : Int,
                        orderDateTime : String, deliveryCost : Int, totalPayment : Int, idProduct : Int,
                   paymentMethod: String, models: List<ResultItemNewOrder?>?,totalGross: Int){
        sharedPref = SharedPreferences(context)
        productEstimateViewModel = ViewModelProvider(this).get(ProductEstimateViewModel::class.java)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_pickup_call,null)
        val dialog = BottomSheetDialog(context)
        val tvEstimateTime = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvStoreName = menuPopup.findViewById<TextView>(R.id.tv_store_name)
        val tvStoreAddress = menuPopup.findViewById<TextView>(R.id.tv_store_address)
        val tvOrderNumber = menuPopup.findViewById<TextView>(R.id.tv_order_number)
        val tvOrderDateTime = menuPopup.findViewById<TextView>(R.id.tv_order_date_time)
        val tvCustomerName = menuPopup.findViewById<TextView>(R.id.tv_customer_name)
        val tvDeliverAddress = menuPopup.findViewById<TextView>(R.id.tv_address)
        val tvPaymentMethod2 = menuPopup.findViewById<TextView>(R.id.tv_delivery_cost)
        val tvPaymentMethod = menuPopup.findViewById<TextView>(R.id.tv_general_payment)
        val tvDeliveryCost = menuPopup.findViewById<TextView>(R.id.value_delivery_cost)
        val tvTotalPrice = menuPopup.findViewById<TextView>(R.id.value_general_payment)
        val tvEstimate = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvConfirm = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val recyclerView = menuPopup.findViewById<RecyclerView>(R.id.recyclerview)
        val tvTotalGross = menuPopup.findViewById<TextView>(R.id.tv_sum_price)
        val recyclerViewPrice = menuPopup.findViewById<RecyclerView>(R.id.recyclerview_voucher)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterProductName = ProductNamePopupAdapter(models!![position])
        recyclerView.adapter = adapterProductName

        recyclerViewPrice.layoutManager = LinearLayoutManager(this)
        Log.d("DEBUG-IDA", "onBindViewHolder: ${models?.get(position)?.orderVoucherHistories?.size}")
        adapterVoucher = VoucherPopupAdapter(models!![position])
        recyclerViewPrice.adapter = adapterVoucher

        Log.d("DATA-PAYMENT-TYPE", "confirmNewOrder-ID ${position}: ${models[position]?.payment?.paymentType}")

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val instant = Instant.parse(orderDateTime)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dateTime = formatter.format(localDateTime)
        val estimateTime = sharedPref.getIntData(Constant.ESTIMATE_TIME)

        if (Constant.ESTIMATE_TIME.equals("estimateTime")){
            tvEstimate.text = "$estimateTime  분"
        }else{
            tvEstimate.text = "$estimateTime  분"
        }
        if (addressUser != null){
            tvDeliverAddress.text = addressUser
        }
        Log.d("PAYMENT-METHOD", "pickuPopup: $paymentMethod")
        when (paymentMethod) {
            "CARD" -> {
                tvPaymentMethod.text = "카드"
            }
            "kakao" -> {
                tvPaymentMethod.text = "카카오페이"
            }
            "Coupon" -> {
                tvPaymentMethod.text = "쿠폰"
            }
            "Subscription" -> {
                tvPaymentMethod.text = "구독"
            }
        }

//        tvPaymentMethod.text = paymentMethod
        tvStoreName.text = storeName
        tvStoreAddress.text = storeAddress
        tvOrderNumber.text = orderNumber.toString()
        tvOrderDateTime.text = dateTime
        tvCustomerName.text = customerName
        tvDeliveryCost.text = deliveryCost.toString()
        if (totalPayment != 0) tvTotalPrice.text = totalPayment.toString()
        else tvTotalPrice.visibility = View.GONE

        tvTotalGross.text = totalGross.toString()

        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        dialog.setContentView(menuPopup)
        imgClose.setOnClickListener {
            dialog!!.dismiss()
        }
        dialog.setOnCancelListener {  }
        dialog.show()


        tvEstimateTime.setOnClickListener {
            popupEstimate(context) { estimateTime ->
                val tvEstimate = dialog.findViewById<TextView>(R.id.tv_choice)
                tvEstimate?.text = "$estimateTime  분"
            }
        }
        tvConfirm.setOnClickListener {
            dialog.dismiss()
            productEstimateViewModel.updateEstimate(idProduct,"making",estimateTime)
        }

        productEstimateViewModel.dataProductEstimate.observe(this){
            Log.d("AFTER-CLICK", "pickuPopup: ${it.message}")
            when(it.message){
                "SUCCESS" -> {
                    getProductByType(orderType = "desc")
                }
                "Token Times Up" -> {
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
                "you dont have access!" ->{
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
            }

        }
    }

    fun popupEstimate(context: Context, onEstimateTimeSelected: (Int) -> Unit) {
        sharedPref = SharedPreferences(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_select_minute_order,null)
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(menuPopup)
        dialog.show()
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val btn5Minutes = menuPopup.findViewById<TextView>(R.id.tv_5_minute)
        val btn10Minutes = menuPopup.findViewById<TextView>(R.id.tv_10_minute)
        val btn15Minutes = menuPopup.findViewById<TextView>(R.id.tv_15_minute)
        val btn20Minutes = menuPopup.findViewById<TextView>(R.id.tv_20_minute)
        val btn25Minutes = menuPopup.findViewById<TextView>(R.id.tv_25_minute)
        val btn30Minutes = menuPopup.findViewById<TextView>(R.id.tv_30_minute)
        val btn35Minutes = menuPopup.findViewById<TextView>(R.id.tv_35_minute)
        val btn40Minutes = menuPopup.findViewById<TextView>(R.id.tv_40_minute)
        val btn45Minutes = menuPopup.findViewById<TextView>(R.id.tv_45_minute)
        val btn50Minutes = menuPopup.findViewById<TextView>(R.id.tv_50_minute)
        val btn55Minutes = menuPopup.findViewById<TextView>(R.id.tv_55_minute)

        btn5Minutes.setOnClickListener {
            selectedEstimateTime = 5
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn10Minutes.setOnClickListener {
            selectedEstimateTime = 10
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn15Minutes.setOnClickListener {
            selectedEstimateTime = 15
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn20Minutes.setOnClickListener {
            selectedEstimateTime = 20
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn25Minutes.setOnClickListener {
            selectedEstimateTime = 25
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn30Minutes.setOnClickListener {
            selectedEstimateTime = 30
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn35Minutes.setOnClickListener {
            selectedEstimateTime = 35
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn40Minutes.setOnClickListener {
            selectedEstimateTime = 40
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn45Minutes.setOnClickListener {
            selectedEstimateTime = 45
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn50Minutes.setOnClickListener {
            selectedEstimateTime = 50
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }
        btn55Minutes.setOnClickListener {
            selectedEstimateTime = 55
            onEstimateTimeSelected(selectedEstimateTime)
            dialog.dismiss()
        }

        imgClose.setOnClickListener { dialog!!.dismiss() }
    }

    fun reasonCancel(context: Context,idProduct: Int) {
        var reasonRejected = ""
        productEstimateViewModel = ViewModelProvider(this).get(ProductEstimateViewModel::class.java)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_cancel_order_reason, null)
        val popup = PopupWindow(
            menuPopup,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )
        val imgOut = menuPopup.findViewById<TextView>(R.id.img_close)
        val checked_hour_over = menuPopup.findViewById<CheckBox>(R.id.checkbox_business_hour_over)
        val checkbox_out_stock = menuPopup.findViewById<CheckBox>(R.id.checkbox_out_stock)
        val checkbox_reason_other = menuPopup.findViewById<CheckBox>(R.id.checkbox_other)
        val edtReason = menuPopup.findViewById<EditText>(R.id.edt_other_reason_cancel)
        val tvConfirm = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val imageOut = menuPopup.findViewById<ConstraintLayout>(R.id.conts_1)

        imageOut.setOnClickListener {
            popup.dismiss()
        }

        checkbox_reason_other.setOnClickListener {
            edtReason.isFocusableInTouchMode = checkbox_reason_other.isChecked
            checkbox_out_stock.isChecked = false
            checked_hour_over.isChecked = false
        }

        checked_hour_over.setOnClickListener {
            reasonRejected= "영업시간이 종료되었습니다."
            checkbox_reason_other.isChecked = false
            checkbox_out_stock.isChecked = false
        }
        checkbox_out_stock.setOnClickListener {
            reasonRejected = "재고가 부족합니다."
            checkbox_reason_other.isChecked = false
            checked_hour_over.isChecked = false
        }

        edtReason.isFocusableInTouchMode = checkbox_reason_other.isChecked

        // Set an empty background to make the popup dismiss when touched outside
        popup.setBackgroundDrawable(null)
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)

        // Set touch interceptor to dismiss the popup when touched outside
        imgOut.setOnClickListener {
            popup.dismiss()
        }
        tvConfirm.setOnClickListener {
            reasonRejected = edtReason.text.toString()
            binding.progressbar.visibility = View.VISIBLE
            productEstimateViewModel.rejectedPickup(idProduct = idProduct, status ="rejected",
                descriptionRejected = reasonRejected)
            popup.dismiss()
        }
        productEstimateViewModel.dataRejected.observe(this){
            Log.d("AFTER-UPDATE", "reasonCancel: ${it.message}")
            when(it.message){
                "SUCCESS" -> {
                    binding.progressbar.visibility = View.GONE
                    getProductByType(orderType = "desc")
                }
                "Token Times Up" -> {
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
                "you dont have access!" ->{
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun filterButton(){
        binding.allOrder.setOnClickListener {
            orderedResults.clear()
            ongoingResult.clear()
            completeResult.clear()
            rejectedResult.clear()
            binding.progressbar.visibility = View.VISIBLE
            binding.tvNewOrder.visibility = View.GONE
            binding.tvProgressOrder.visibility = View.GONE
            binding.tvCompleteOrder.visibility = View.GONE
            binding.tvRejectedOrder.visibility = View.GONE
            binding.recyclerNewOrder.visibility = View.GONE
            binding.recyclerProgressOrder.visibility = View.GONE
            binding.recyclerCompleteOrder.visibility = View.GONE
            binding.recyclerRejectOrder.visibility = View.GONE
            getProductByType(orderType = "desc")
            binding.allOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.allOrder.setTextColor(Color.WHITE)
            binding.pickupOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.pickupOrder.setTextColor(Color.BLACK)
            binding.deliveryOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.deliveryOrder.setTextColor(Color.BLACK)
            binding.dineIn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.dineIn.setTextColor(Color.BLACK)
        }
        binding.pickupOrder.setOnClickListener {
            orderedResults.clear()
            ongoingResult.clear()
            completeResult.clear()
            rejectedResult.clear()
            binding.progressbar.visibility = View.VISIBLE
            binding.tvNewOrder.visibility = View.GONE
            binding.tvProgressOrder.visibility = View.GONE
            binding.tvCompleteOrder.visibility = View.GONE
            binding.tvRejectedOrder.visibility = View.GONE
            binding.recyclerNewOrder.visibility = View.GONE
            binding.recyclerProgressOrder.visibility = View.GONE
            binding.recyclerCompleteOrder.visibility = View.GONE
            binding.recyclerRejectOrder.visibility = View.GONE
            getProductByType(typeOrder = "pickup")
            binding.allOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.allOrder.setTextColor(Color.BLACK)
            binding.pickupOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.pickupOrder.setTextColor(Color.WHITE)
            binding.deliveryOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.deliveryOrder.setTextColor(Color.BLACK)
            binding.dineIn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.dineIn.setTextColor(Color.BLACK)
        }
        binding.deliveryOrder.setOnClickListener {
            orderedResults.clear()
            ongoingResult.clear()
            completeResult.clear()
            rejectedResult.clear()
            binding.progressbar.visibility = View.VISIBLE
            binding.tvNewOrder.visibility = View.GONE
            binding.tvProgressOrder.visibility = View.GONE
            binding.tvCompleteOrder.visibility = View.GONE
            binding.tvRejectedOrder.visibility = View.GONE
            binding.recyclerNewOrder.visibility = View.GONE
            binding.recyclerProgressOrder.visibility = View.GONE
            binding.recyclerCompleteOrder.visibility = View.GONE
            binding.recyclerRejectOrder.visibility = View.GONE
            getProductByType(typeOrder = "delivery")
            binding.allOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.allOrder.setTextColor(Color.BLACK)
            binding.pickupOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.pickupOrder.setTextColor(Color.BLACK)
            binding.deliveryOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.deliveryOrder.setTextColor(Color.WHITE)
            binding.dineIn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.dineIn.setTextColor(Color.BLACK)
        }
        binding.dineIn.setOnClickListener {
            orderedResults.clear()
            ongoingResult.clear()
            completeResult.clear()
            rejectedResult.clear()
            binding.progressbar.visibility = View.VISIBLE
            binding.tvNewOrder.visibility = View.GONE
            binding.tvProgressOrder.visibility = View.GONE
            binding.tvCompleteOrder.visibility = View.GONE
            binding.tvRejectedOrder.visibility = View.GONE
            binding.recyclerNewOrder.visibility = View.GONE
            binding.recyclerProgressOrder.visibility = View.GONE
            binding.recyclerCompleteOrder.visibility = View.GONE
            binding.recyclerRejectOrder.visibility = View.GONE
            getProductByType(typeOrder = "dinein")
            binding.allOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.allOrder.setTextColor(Color.BLACK)
            binding.pickupOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.pickupOrder.setTextColor(Color.BLACK)
            binding.deliveryOrder.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            binding.deliveryOrder.setTextColor(Color.BLACK)
            binding.dineIn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.dineIn.setTextColor(Color.WHITE)
        }
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}