package com.restaurant.caffeinapplication.ui.view.activity

import Popup
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.adapter.ProductCompleteOrderAdapter
import com.restaurant.caffeinapplication.adapter.ProductNamePopupAdapter
import com.restaurant.caffeinapplication.adapter.ProductNewOrderAdapter
import com.restaurant.caffeinapplication.adapter.ProductOngoingAdapter
import com.restaurant.caffeinapplication.data.model.ResultItemNewOrder
import com.restaurant.caffeinapplication.data.network.FirebaseMessageService
import com.restaurant.caffeinapplication.databinding.ActivityOrderBinding
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
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class OrderActivity : AppCompatActivity(), Popup.PopupCallback {

    lateinit var binding : ActivityOrderBinding
    lateinit var adapter : ProductNewOrderAdapter
    lateinit var adapterOngoing : ProductOngoingAdapter
    lateinit var adapterComplete : ProductCompleteOrderAdapter
    lateinit var adapterProductName : ProductNamePopupAdapter
    lateinit var viewModelHome : ProductHomeViewModel
    lateinit var sharedPref : SharedPreferences
    private var selectedEstimateTime: Int = 0
    private lateinit var productEstimateViewModel : ProductEstimateViewModel
    var orderedResults = mutableListOf<ResultItemNewOrder?>()
    var ongoingResult = mutableListOf<ResultItemNewOrder?>()
    var completeResult = mutableListOf<ResultItemNewOrder?>()

    var popup = Popup()
    var idCategory = 0
    var statusCategoryFilter = ""
    var type = "collection"
    var orderBy = "createdAt"
    var starDateTime = ""
    var endDateTime = ""
    var orderType = "ASC"
    var starYear = ""
    var starMonth = ""
    var starDate = ""
    var endYear = ""
    var endMonth = ""
    var endDate = ""
    var typeOrder = ""
    var orderFrom = ""
    var userOrder = ""
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var qrScanViewModel : QrScanViewModel

    companion object {
        lateinit var barcodeLauncher: ActivityResultLauncher<ScanOptions>
        var isCouponOrVoucherClaimedShown = false
        var isToastShown = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelHome = ViewModelProvider(this)[ProductHomeViewModel::class.java]
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        qrScanViewModel = ViewModelProvider(this)[QrScanViewModel::class.java]

        barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
            Log.d("QR-SCANNNN-ORDER", ": ${result.contents}")
            if (result.contents == null) {
                val originalIntent = result.originalIntent
                if (originalIntent == null) {
                } else if (originalIntent.hasExtra(Intents.Scan.MISSING_CAMERA_PERMISSION)) {
                    Toast.makeText(this, "카메라 접근 권한이 필요 합니다.\n" +
                            "설정에서 접근 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
                }
            } else {
                qrScanner(result.contents)
                Log.d("QR-SCANNNN-ORDER", ": ${result.contents}")
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


        sharedPref = SharedPreferences(this)
        binding.progressbar.visibility = View.VISIBLE
        getProductByType(orderType = "desc")

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val current = LocalDateTime.now().format(formatter)
        val yearOrder = current.takeLast(4)
        val monthOrder = current.substring(3,5)
        val dateOrder = current.take(2)

        binding.tvCurrentDate.text = "${yearOrder}년 ${monthOrder}월 ${dateOrder}일"

        var sideMenu = findViewById<ConstraintLayout>(R.id.btn_side_menu)
        sideMenu.setOnClickListener {
            popup.sideMenuPopup(this,this,this,settingViewModel,
                sharedPref.getBooleanData(Constant.IS_OPEN), barcodeLauncher)
        }
        idCategory = intent.getIntExtra("ORDER-CATEGORY",0)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.pullToRefresh.setOnRefreshListener {
            binding.pullToRefresh.isRefreshing = false
            getProductByType(orderType = "desc")
        }


        when(idCategory) {
            100 ->{
                statusCategoryFilter = "ordered"
                binding.spinnerStatus.text = "새로 들어온 주문"
                binding.cardCompleteOrder.visibility = View.GONE
            }
            200 -> {
                statusCategoryFilter = "delivery"
                binding.spinnerStatus.text = "진행중인 주문"
                binding.cardCompleteOrder.visibility = View.GONE
            }
            300 -> {
                statusCategoryFilter = "finished"
                binding.spinnerStatus.text = "필터"
                binding.cardCompleteOrder.visibility = View.GONE
            }
        }

        var isCardVisible = false
        binding.spinnerStatus.setOnClickListener {
            if (isCardVisible) {
                binding.cardCompleteOrder.visibility = View.GONE
            } else {
                binding.cardCompleteOrder.visibility = View.VISIBLE
                binding.typeOrderAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
                binding.typeOrderAll.setTextColor(Color.WHITE)
                orderFrom = "all"
                typeOrder = "all"
                binding.typeOrderFromAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
                binding.typeOrderFromAll.setTextColor(Color.WHITE)
            }

            // Update the boolean variable to toggle the state
            isCardVisible = !isCardVisible
        }

        // Mendapatkan tanggal dan bulan saat ini
        val currentDateTime = LocalDateTime.now()
        val currentYear = currentDateTime.year
        val currentMonth = currentDateTime.monthValue
        val currentDate = currentDateTime.dayOfMonth

// Mengatur spinner tahun
        val yearAdapter = ArrayAdapter.createFromResource(this, R.array.year, android.R.layout.simple_spinner_item)
        binding.spinnerStartdateYear.adapter = yearAdapter
        binding.spinnerEndateYear.adapter = yearAdapter
        val yearPosition = yearAdapter.getPosition(currentYear.toString())
        binding.spinnerStartdateYear.setSelection(yearPosition)
        binding.spinnerEndateYear.setSelection(yearPosition)

// Mengatur spinner bulan
        val monthAdapter = ArrayAdapter.createFromResource(this, R.array.month, android.R.layout.simple_spinner_item)
        binding.spinnerStartdateMonth.adapter = monthAdapter
        binding.spinnerEndateMonth.adapter = monthAdapter
        val monthPosition = currentMonth - 1 // Bulan dimulai dari 0 (Januari) hingga 11 (Desember)
        binding.spinnerStartdateMonth.setSelection(monthPosition)
        binding.spinnerEndateMonth.setSelection(monthPosition)

// Mengatur spinner tanggal
        val daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
        val dayArray = (1..daysInMonth).toList().map { it.toString().padStart(2, '0') }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dayArray)
        binding.spinnerStartdateDate.adapter = dayAdapter
        binding.spinnerEndateDate.adapter = dayAdapter

// Format tanggal saat ini
        val currentDateFormatted = currentDate.toString().padStart(2, '0')

// Cari posisi tanggal saat ini dalam array yang telah diformat
        val datePosition = dayArray.indexOf(currentDateFormatted)

// Set posisi spinner ke tanggal saat ini
        binding.spinnerStartdateDate.setSelection(datePosition)
        binding.spinnerEndateDate.setSelection(datePosition)

        binding.spinnerStartdateYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                starYear = p0?.getItemAtPosition(p3.toInt()).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.spinnerStartdateMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                starMonth = p0?.getItemAtPosition(p3.toInt()).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.spinnerStartdateDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                starDate = p0?.getItemAtPosition(p3.toInt()).toString()

            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.spinnerEndateYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                endYear = p0?.getItemAtPosition(p3.toInt()).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.spinnerEndateMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                endMonth = p0?.getItemAtPosition(p3.toInt()).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.spinnerEndateDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                endDate = p0?.getItemAtPosition(p3.toInt()).toString()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.typeOrderAll.setOnClickListener {
            binding.typeOrderAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.typeOrderAll.setTextColor(Color.WHITE)
            binding.typeOrderDelivery.setTextColor(Color.BLACK)
            binding.typeOrderPickup.setTextColor(Color.BLACK)
            binding.typeOrderDelivery.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderPickup.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderDinein.setTextColor(Color.BLACK)
            binding.typeOrderDinein.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
        }
        binding.typeOrderDelivery.setOnClickListener {
            typeOrder = "delivery"
            binding.typeOrderDelivery.setTextColor(Color.WHITE)
            binding.typeOrderAll.setTextColor(Color.BLACK)
            binding.typeOrderPickup.setTextColor(Color.BLACK)
            binding.typeOrderAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderDelivery.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.typeOrderPickup.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            Log.d("TYPE-ORDER", "onCreate: $typeOrder")
            binding.typeOrderDinein.setTextColor(Color.BLACK)
            binding.typeOrderDinein.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
        }
        binding.typeOrderPickup.setOnClickListener {
            typeOrder = "pickup"
            binding.typeOrderPickup.setTextColor(Color.WHITE)
            binding.typeOrderAll.setTextColor(Color.BLACK)
            binding.typeOrderDelivery.setTextColor(Color.BLACK)
            binding.typeOrderAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderDelivery.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderPickup.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            Log.d("TYPE-ORDER", "onCreate: $typeOrder")
            binding.typeOrderDinein.setTextColor(Color.BLACK)
            binding.typeOrderDinein.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
        }
        binding.typeOrderDinein.setOnClickListener {
            typeOrder = "dinein"
            binding.typeOrderDinein.setTextColor(Color.WHITE)
            binding.typeOrderPickup.setTextColor(Color.BLACK)
            binding.typeOrderAll.setTextColor(Color.BLACK)
            binding.typeOrderDelivery.setTextColor(Color.BLACK)
            binding.typeOrderAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderDelivery.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderPickup.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderDinein.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
        }
        binding.typeOrderFromAll.setOnClickListener {
            orderFrom = "all"
            binding.typeOrderFromAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.typeOrderFromAll.setTextColor(Color.WHITE)
            binding.typeOrderFromSubscription.setTextColor(Color.BLACK)
            binding.typeOrderFromVoucher.setTextColor(Color.BLACK)
            binding.typeOrderFromCoupon.setTextColor(Color.BLACK)
            binding.typeOrderFromSubscription.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromVoucher.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromCoupon.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            Log.d("FROM-ORDER", "onCreate: $orderFrom")

        }
        binding.typeOrderFromSubscription.setOnClickListener {
            orderFrom = "subcription"
            binding.typeOrderFromSubscription.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.typeOrderFromSubscription.setTextColor(Color.WHITE)
            binding.typeOrderFromAll.setTextColor(Color.BLACK)
            binding.typeOrderFromVoucher.setTextColor(Color.BLACK)
            binding.typeOrderFromCoupon.setTextColor(Color.BLACK)
            binding.typeOrderFromAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromVoucher.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromCoupon.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            Log.d("FROM-ORDER", "onCreate: $orderFrom")
        }
        binding.typeOrderFromVoucher.setOnClickListener {
            orderFrom = "voucher"
            binding.typeOrderFromVoucher.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.typeOrderFromVoucher.setTextColor(Color.WHITE)
            binding.typeOrderFromAll.setTextColor(Color.BLACK)
            binding.typeOrderFromSubscription.setTextColor(Color.BLACK)
            binding.typeOrderFromCoupon.setTextColor(Color.BLACK)
            binding.typeOrderFromAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromSubscription.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromCoupon.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            Log.d("FROM-ORDER", "onCreate: $orderFrom")
        }
        binding.typeOrderFromCoupon.setOnClickListener {
            orderFrom = "coupon"
            binding.typeOrderFromCoupon.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            binding.typeOrderFromCoupon.setTextColor(Color.WHITE)
            binding.typeOrderFromAll.setTextColor(Color.BLACK)
            binding.typeOrderFromSubscription.setTextColor(Color.BLACK)
            binding.typeOrderFromVoucher.setTextColor(Color.BLACK)
            binding.typeOrderFromAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromSubscription.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromVoucher.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            Log.d("FROM-ORDER", "onCreate: $orderFrom")
        }

        binding.btnFilter.setOnClickListener {
            starDateTime = "${starYear}-${starMonth}-${starDate}"
            endDateTime = "${endYear}-${endMonth}-${endDate}"
            userOrder = binding.edtUser.text.toString()

            if (!starDateTime.contains("2000") && !endDateTime.contains("2000")){
                if (!binding.edtUser.text.isNullOrEmpty() && orderFrom == "" && typeOrder == ""){
                    getProductByFilter(starDateTime,endDateTime,name = userOrder, orderType = orderType, type = type)
                } else if (orderFrom != "" && binding.edtUser.text.isNullOrEmpty() && typeOrder == "" ){
                    getProductByFilter(starDateTime,endDateTime,orderFrom = orderFrom,orderType = orderType,type = type)
                }else if (typeOrder != "" && orderFrom == "" && binding.edtUser.text.isNullOrEmpty() ){
                    getProductByFilter(starDateTime,endDateTime,typeOrder = typeOrder,orderType = orderType,type = type)
                }else {
                    getProductByFilter(starDateTime,endDateTime, name = userOrder, orderFrom = orderFrom, typeOrder = typeOrder,orderType = orderType)
                }
            } else if (orderFrom != "") {
                if (!binding.edtUser.text.isNullOrEmpty() && typeOrder == ""){
                    getProductByFilter(name = userOrder, orderFrom = orderFrom,orderType = orderType, type = type)
                }else if (typeOrder != "" && binding.edtUser.text.isNullOrEmpty() ){
                    getProductByFilter(orderFrom = orderFrom,typeOrder = typeOrder,orderType = orderType, type = type)
                }else {
                    getProductByFilter(name = userOrder, orderFrom = orderFrom, typeOrder = typeOrder,orderType = orderType, type = type)
                }
            }else  if (!binding.edtUser.text.isNullOrEmpty()){
                getProductByFilter(name = userOrder,orderType = orderType, type = type)
            }else if (typeOrder != ""){
                getProductByFilter(typeOrder = typeOrder,orderType = orderType, type = type)
            }
        }
        binding.btnReset.setOnClickListener {
            orderFrom = "all"
            typeOrder = "all"
            binding.typeOrderAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderAll.setTextColor(Color.BLACK)
            binding.typeOrderDelivery.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderDelivery.setTextColor(Color.BLACK)
            binding.typeOrderPickup.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderPickup.setTextColor(Color.BLACK)
            binding.typeOrderFromSubscription.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromSubscription.setTextColor(Color.BLACK)
            binding.typeOrderFromAll.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromAll.setTextColor(Color.BLACK)
            binding.typeOrderFromVoucher.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromVoucher.setTextColor(Color.BLACK)
            binding.typeOrderFromCoupon.backgroundTintList = ContextCompat.getColorStateList(this, R.color.bg_text_view)
            binding.typeOrderFromCoupon.setTextColor(Color.BLACK)
            binding.edtUser.setText(null)
            binding.spinnerStartdateYear.setSelection(0)
            binding.spinnerStartdateDate.setSelection(0)
            binding.spinnerStartdateMonth.setSelection(0)

            binding.spinnerEndateYear.setSelection(0)
            binding.spinnerEndateMonth.setSelection(0)
            binding.spinnerEndateDate.setSelection(0)
            getProductByType(orderType = "desc")
        }

        binding.spinnerLatestOrder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> {
                        orderType = "ASC"
                        orderedResults.clear()
                        ongoingResult.clear()
                        completeResult.clear()
                        getProductByStatus(statusCategoryFilter,type, orderBy, orderType)
                    }
                    1 -> {
                        orderedResults.clear()
                        ongoingResult.clear()
                        completeResult.clear()
                        Log.d("DATA-NEW-ORDER-AC", "onBindViewHolder: CATEGORY $statusCategoryFilter, type $type oderd by $orderBy, order rtype $orderType")
                        orderType = "DESC"
                        getProductByStatus(statusCategoryFilter,type, orderBy, orderType)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        viewModelHome.productHomeDataByFilter.observe(this){
            when (it.message){
                "SUCCESS" -> {
                    orderedResults.clear()
                    ongoingResult.clear()
                    completeResult.clear()
                    binding.progressbar.visibility = View.GONE

                    Log.d("DATA-NEW-ORDER-AC", "onBindViewHolder: ${it.data!!.result!!.size}")

                    for (item in it.data!!.result!!) {
                        if (item!!.status.equals("ordered", ignoreCase = true)) {
                            Log.d("DATA-NEW-ORDER-AC", "onBindViewHolder: ${item}")
                            orderedResults.add(item)
                        }else if (item.status.equals("making") || item.status.equals("delivery") || item.status.equals("pickup")) {
                            ongoingResult.add(item)
                        }else if (item.status.equals("finished")) {
                            completeResult.add(item)
                        }
                    }
                    adapter = ProductNewOrderAdapter(orderedResults)
                    adapterOngoing = ProductOngoingAdapter(ongoingResult)
                    adapterComplete = ProductCompleteOrderAdapter(completeResult)

                    adapter.setItemClickListener(object : ProductNewOrderAdapter.ItemClickListener {
                        override fun onClick(view: View, position: Int,idProduct: Int) {
                            reasonCancel(this@OrderActivity,idProduct)
                        }
                    })

                    adapter.setItemClickListener(object : ProductNewOrderAdapter.ItemClickListener2 {
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClick(view: View, position: Int, customerName: String,
                                             addressUser: String, productName: String, priceProduct: Int,
                                             orderNumber: Int, dateTime : String, deliveryCost : Int,
                                             totalPayment : Int, idProduct : Int,
                                             models : List<ResultItemNewOrder?>?, type: String,
                                             paymentMethod: String,totalGross: Int) {
                            if (!type.equals("pickup")){
                                confirmNewOrder(this@OrderActivity,position,customerName,addressUser,productName,
                                    priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                    sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,
                                    dateTime,deliveryCost,totalPayment, idProduct,models,totalGross,paymentMethod)
                            }else{
                                pickuPopup(this@OrderActivity,position,customerName,addressUser,productName,
                                    priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                    sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,dateTime,
                                    deliveryCost,totalPayment, idProduct,paymentMethod,models,totalGross)
                            }
                        }
                    })
                    adapterOngoing.setItemClickListener(object : ProductOngoingAdapter.ItemClickListener{
                        override fun onClick(view: View, position: Int, idProduct: Int,type: String) {
                            popupConfirmDelivery(this@OrderActivity, idProduct,"making")
                        }
                    })
                    adapterOngoing.setItemClickListener(object : ProductOngoingAdapter.ItemClickListener3{
                        override fun onClickPickup(view: View, position: Int, idProduct: Int) {
                            popupConfirmDelivery(this@OrderActivity, idProduct,"pickup")
                        }
                        override fun pickupOnly(view: View, position: Int, idProduct: Int, type: String) {
                        }
                    })
                    adapterOngoing.setItemClickListener(object : ProductOngoingAdapter.ItemClickListener2{
                        override fun onClickDelivery(view: View, position: Int, idProduct: Int) {
                            popupConfirmDelivery(this@OrderActivity, idProduct,"delivery")
                        }
                    })

                    adapterComplete.setItemClickListener(object : ProductCompleteOrderAdapter.ItemClickListener{
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClickDeliverOrder(view: View, position: Int, customerName: String,
                                                         addressUser: String, productName: String,
                                                         priceProduct: Int, orderNumber: Int,
                                                         dateTime: String, deliveryCost: Int,
                                                         totalPayment: Int,models : List<ResultItemNewOrder?>?,
                                                         totalGross: Int) {
                            popup.deliveryOrderDetails(this@OrderActivity,position,customerName,
                                addressUser,productName, priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,
                                dateTime,deliveryCost,totalPayment,models,totalGross)
                        }
                    })
                    adapterComplete.setItemClickListener(object : ProductCompleteOrderAdapter.ItemClickListener2{
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onClickPackagingOrder(view: View, position: Int, customerName: String,
                                                           productName: String, priceProduct: Int, orderNumber: Int,
                                                           dateTime: String, deliveryCost: Int,
                                                           totalPayment: Int, paymentMethod: String,
                                                           models : List<ResultItemNewOrder?>?,
                                                           totalGross: Int) {
                            popup.packagingOrderDetails(this@OrderActivity,position,customerName,
                                productName, priceProduct,sharedPref.getStringData(Constant.NAME_COMPANY)!!,
                                sharedPref.getStringData(Constant.ADDRESS_COMPANY)!!,orderNumber,
                                dateTime,deliveryCost,totalPayment,paymentMethod,models,totalGross)
                        }

                    })
                    when(idCategory) {
                        100 ->{
                            binding.recyclerView.adapter = adapter
                            binding.cardCompleteOrder.visibility = View.GONE
                        }
                        200 -> {
                            binding.recyclerView.adapter = adapterOngoing
                            binding.cardCompleteOrder.visibility = View.GONE
                        }
                        300 -> {
                            binding.recyclerView.adapter = adapterComplete
                            binding.cardCompleteOrder.visibility = View.GONE
                        }
                    }
                }
                "Token Times Up" -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(this, "Token Times Up, please login first", Toast.LENGTH_LONG).show()
                }
                "you dont have access!" ->{
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(this, "you dont have access, please login first!", Toast.LENGTH_SHORT).show()
                }
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
                    getProductByType(orderType = "desc")
                }
                "Coupon or Voucher Already Claimed" ->{
                    if (HomeActivity.isCouponOrVoucherClaimedShown && !HomeActivity.isToastShown){
                        Toast.makeText(this, "구독권, 기프티콘이 이미 사용되었습니다", Toast.LENGTH_SHORT).show()
                        HomeActivity.isToastShown = true
                    }
                }
                "Total Amount Cannot Be More Than Voucher Amount" -> {
                    if (HomeActivity.isCouponOrVoucherClaimedShown && !HomeActivity.isToastShown) {
                        Toast.makeText(this, "Total Amount Cannot Be More Than Voucher Amount",
                            Toast.LENGTH_SHORT).show()
                        HomeActivity.isToastShown = true
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

    fun qrScanner(codeQr:String){
        qrScanViewModel.qrScanPreview(codeQr)
    }

    private fun getProductByType(orderType: String?= null, typeOrder : String?= null){
        viewModelHome.getProductByFilter(orderType = orderType, typeOrder = typeOrder)
    }
    private fun getProductByStatus(status : String,type : String?=null,orderBy : String?=null,orderType : String?=null){
        viewModelHome.getProductByStatus(status,type,orderBy, orderType)
    }
    private fun getProductByFilter(starDate : String? = null, endDate : String? = null,typeOrder : String? = null,
                                   orderFrom : String? = null, name : String? = null,status : String? = null,
                                   type : String?=null,orderBy : String?=null,orderType : String?=null){
        viewModelHome.getProductByFilter(starDate =starDate , endDate =endDate, typeOrder = typeOrder,
            orderFrom = orderFrom, name = name)
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
            productEstimateViewModel.rejectedPickup(idProduct = idProduct, status ="rejected", descriptionRejected = reasonRejected)
            popup.dismiss()
        }
        productEstimateViewModel.dataRejected.observe(this){
            when(it.message){
                "SUCCESS" -> {
                    binding.progressbar.visibility = View.GONE
                    getProductByType(orderType = "desc")
                }
                "Token Times Up" -> {
                    Toast.makeText(this, "Token Times Up, please login first", Toast.LENGTH_LONG).show()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access, please login first!", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
        val recyclerView = menuPopup.findViewById<RecyclerView>(R.id.recyclerview)
        val tvTotalGross = menuPopup.findViewById<TextView>(R.id.tv_sum_price)
        val tvPaymentMethod = menuPopup.findViewById<TextView>(R.id.tv_general_payment)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterProductName = ProductNamePopupAdapter(models!![position])
        recyclerView.adapter = adapterProductName

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
                    Toast.makeText(this, "Token Times Up, please login first", Toast.LENGTH_LONG).show()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access, please login first!", Toast.LENGTH_SHORT).show()
                }
            }

        }
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
        val tvDeliveryCost = menuPopup.findViewById<TextView>(R.id.value_delivery_cost)
        val tvTotalPrice = menuPopup.findViewById<TextView>(R.id.value_general_payment)
        val tvEstimate = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvConfirm = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val recyclerView = menuPopup.findViewById<RecyclerView>(R.id.recyclerview)
        val tvTotalGross = menuPopup.findViewById<TextView>(R.id.tv_sum_price)
        val tvPaymentMethod = menuPopup.findViewById<TextView>(R.id.tv_general_payment)


        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterProductName = ProductNamePopupAdapter(models!![position])
        recyclerView.adapter = adapterProductName

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
                    Toast.makeText(this, "Token Times Up, please login first", Toast.LENGTH_LONG).show()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access, please login first!", Toast.LENGTH_SHORT).show()
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

    fun popupConfirmDelivery( context: Context,idProduct: Int,status : String ) {
        productEstimateViewModel = ViewModelProvider(this).get(ProductEstimateViewModel::class.java)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_confirm_pickup, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonSubmit = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        imgClose.setOnClickListener {
            popup.dismiss()
        }
        when(status){
            "making"-> {
                buttonSubmit.setOnClickListener {
                    binding.progressbar.visibility = View.VISIBLE
                    productEstimateViewModel.updatePickup(idProduct,"pickup")
                }
            }
            "pickup"-> {
                buttonSubmit.setOnClickListener {
                    binding.progressbar.visibility = View.VISIBLE
                    productEstimateViewModel.updatePickup(idProduct,"delivery")
                }
            }
            "delivery" -> {
                buttonSubmit.setOnClickListener {
                    binding.progressbar.visibility = View.VISIBLE
                    productEstimateViewModel.updatePickup(idProduct,"finished")
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
                    Toast.makeText(this, "Token Times Up, please login first", Toast.LENGTH_LONG).show()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access, please login first!", Toast.LENGTH_SHORT).show()
                }
            }

        }

        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)

    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}