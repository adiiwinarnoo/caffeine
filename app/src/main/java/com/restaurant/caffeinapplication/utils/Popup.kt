import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.adapter.IncomeProductNameAdapter
import com.restaurant.caffeinapplication.adapter.ProductNamePopupAdapter
import com.restaurant.caffeinapplication.data.model.DataOrderItem
import com.restaurant.caffeinapplication.data.model.ResultItemNewOrder
import com.restaurant.caffeinapplication.ui.view.activity.*
import com.restaurant.caffeinapplication.ui.view.activity.ui.IncomeCouponActivity
import com.restaurant.caffeinapplication.ui.view.activity.ui.IncomeSubscribeActivity
import com.restaurant.caffeinapplication.ui.view.activity.ui.OrderIncomeActivity
import com.restaurant.caffeinapplication.ui.view.activity.ui.StocksActivity
import com.restaurant.caffeinapplication.ui.viewModel.ProductEstimateViewModel
import com.restaurant.caffeinapplication.ui.viewModel.QrScanViewModel
import com.restaurant.caffeinapplication.ui.viewModel.SettingViewModel
import com.restaurant.caffeinapplication.utils.Constant
import com.restaurant.caffeinapplication.utils.ScanOptions
import com.restaurant.caffeinapplication.utils.SharedPreferences
import com.google.android.material.bottomsheet.BottomSheetDialog
import de.hdodenhof.circleimageview.CircleImageView
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Popup : AppCompatActivity() {

    lateinit var sharedPref : SharedPreferences

    private var selectedEstimateTime: Int = 0
    private lateinit var productEstimateViewModel : ProductEstimateViewModel
    private lateinit var qrScanViewModel : QrScanViewModel

    private lateinit var switchStatusStore: SwitchCompat
    private lateinit var tvStoreStatus: TextView



    @RequiresApi(Build.VERSION_CODES.O)
    fun deliveryOrderDetails(context: Context,position : Int,customerName : String, addressUser : String,
                             productName : String, priceProduct : Int, storeName : String,
                             storeAddress : String, orderNumber : Int, orderDateTime : String,
                             deliveryCost : Int, totalPayment : Int, models : List<ResultItemNewOrder?>?,
                             totalGross: Int){
        sharedPref = SharedPreferences(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_completed_order,null)
        val dialog = BottomSheetDialog(context)
        val tvEstimateTime = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvStoreName = menuPopup.findViewById<TextView>(R.id.tv_store_name)
        val tvStoreAddress = menuPopup.findViewById<TextView>(R.id.tv_store_address)
        val tvOrderNumber = menuPopup.findViewById<TextView>(R.id.tv_order_number)
        val tvOrderDateTime = menuPopup.findViewById<TextView>(R.id.tv_order_date_time)
        val tvCustomerName = menuPopup.findViewById<TextView>(R.id.tv_customer_name)
        val tvDeliverAddress = menuPopup.findViewById<TextView>(R.id.tv_address)
        val tvProductOption = menuPopup.findViewById<TextView>(R.id.tv_option)
        val tvProductPrice = menuPopup.findViewById<TextView>(R.id.tv_value_price)
        val tvDeliveryCost = menuPopup.findViewById<TextView>(R.id.value_delivery_cost)
        val tvTotalPrice = menuPopup.findViewById<TextView>(R.id.value_general_payment)
        val tvEstimate = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvConfirm = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val tvValuePrice = menuPopup.findViewById<TextView>(R.id.tv_value_price)
        val recyclerView = menuPopup.findViewById<RecyclerView>(R.id.recyclerview)
        val tvTotalGross = menuPopup.findViewById<TextView>(R.id.tv_sum_price)

        val layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = ProductNamePopupAdapter(models!![position])

        recyclerView.adapter = adapter




        tvStoreName.text = storeName
        tvDeliverAddress.text = addressUser
        tvStoreAddress.text = storeAddress
        tvOrderNumber.text = orderNumber.toString()
        tvCustomerName.text = customerName
//        tvProductOption.text = productName
        Log.d("PRICE-", "deliveryOrderDetails: $priceProduct")
//        tvProductPrice.text = priceProduct.toString()
        tvDeliveryCost.text = deliveryCost.toString()
        tvTotalPrice.text = totalPayment.toString()
//        tvValuePrice.text = priceProduct.toString()
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
    fun deliveryOrderDetailsIncome(context: Context, position : Int, customerName : String, addressUser : String,
                                   productName : String, priceProduct : Int, storeName : String,
                                   storeAddress : String, orderNumber : Int, orderDateTime : String,
                                   deliveryCost : Int, totalPayment : Int, models : List<DataOrderItem?>?,
                                   totalGross: Int,paymentMethod: String){
        sharedPref = SharedPreferences(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_completed_order,null)
        val dialog = BottomSheetDialog(context)
        val tvEstimateTime = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvStoreName = menuPopup.findViewById<TextView>(R.id.tv_store_name)
        val tvStoreAddress = menuPopup.findViewById<TextView>(R.id.tv_store_address)
        val tvOrderNumber = menuPopup.findViewById<TextView>(R.id.tv_order_number)
        val tvOrderDateTime = menuPopup.findViewById<TextView>(R.id.tv_order_date_time)
        val tvCustomerName = menuPopup.findViewById<TextView>(R.id.tv_customer_name)
        val tvDeliverAddress = menuPopup.findViewById<TextView>(R.id.tv_address)
        val tvProductOption = menuPopup.findViewById<TextView>(R.id.tv_option)
        val tvProductPrice = menuPopup.findViewById<TextView>(R.id.tv_value_price)
        val tvDeliveryCost = menuPopup.findViewById<TextView>(R.id.value_delivery_cost)
        val tvTotalPrice = menuPopup.findViewById<TextView>(R.id.value_general_payment)
        val tvEstimate = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvConfirm = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val tvValuePrice = menuPopup.findViewById<TextView>(R.id.tv_value_price)
        val recyclerView = menuPopup.findViewById<RecyclerView>(R.id.recyclerview)
        val tvTotalGross = menuPopup.findViewById<TextView>(R.id.tv_sum_price)
        val tvPaymentMethod = menuPopup.findViewById<TextView>(R.id.tv_general_payment)

        val layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val dataOrder = models!![position]
        val adapter = IncomeProductNameAdapter(dataOrder)

        recyclerView.adapter = adapter

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
        tvDeliverAddress.text = addressUser
        tvStoreAddress.text = storeAddress
        tvOrderNumber.text = orderNumber.toString()
        tvCustomerName.text = customerName
//        tvProductOption.text = productName
        Log.d("PRICE-", "deliveryOrderDetails: $priceProduct")
//        tvProductPrice.text = priceProduct.toString()
        tvDeliveryCost.text = deliveryCost.toString()
        if (totalPayment != 0) tvTotalPrice.text = totalPayment.toString()
        else tvTotalPrice.visibility = View.GONE
//        tvValuePrice.text = priceProduct.toString()
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
    fun packagingOrderDetails(context: Context,position: Int,customerName : String,
                             productName : String, priceProduct : Int, storeName : String,
                             storeAddress : String, orderNumber : Int, orderDateTime : String,
                             deliveryCost : Int, totalPayment : Int,paymentMethod : String,
                              models : List<ResultItemNewOrder?>?,totalGross: Int){
        sharedPref = SharedPreferences(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_packaging_order,null)
        val dialog = BottomSheetDialog(context)
        val tvEstimateTime = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvValuePrice = menuPopup.findViewById<TextView>(R.id.tv_value_price)
        val tvStoreName = menuPopup.findViewById<TextView>(R.id.tv_store_name)
        val tvStoreAddress = menuPopup.findViewById<TextView>(R.id.tv_store_address)
        val tvOrderNumber = menuPopup.findViewById<TextView>(R.id.tv_order_number)
        val tvOrderDateTime = menuPopup.findViewById<TextView>(R.id.tv_order_date_time)
        val tvCustomerName = menuPopup.findViewById<TextView>(R.id.tv_customer_name)
        val tvDeliverAddress = menuPopup.findViewById<TextView>(R.id.tv_address)
        val tvProductOption = menuPopup.findViewById<TextView>(R.id.tv_option)
        val tvProductPrice = menuPopup.findViewById<TextView>(R.id.tv_value_price)
        val tvDeliveryCost = menuPopup.findViewById<TextView>(R.id.value_delivery_cost)
        val tvTotalPrice = menuPopup.findViewById<TextView>(R.id.value_general_payment)
        val tvEstimate = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvConfirm = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val tvPaymentMethod = menuPopup.findViewById<TextView>(R.id.tv_general_payment)
        val recyclerView = menuPopup.findViewById<RecyclerView>(R.id.recyclerview)
        val tvTotalGross = menuPopup.findViewById<TextView>(R.id.tv_sum_price)



        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val instant = Instant.parse(orderDateTime)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dateTime = formatter.format(localDateTime)
        val estimateTime = sharedPref.getIntData(Constant.ESTIMATE_TIME)

        val layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val adapter = ProductNamePopupAdapter(models!![position])

        recyclerView.adapter = adapter

        tvOrderDateTime.text = dateTime
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
        tvCustomerName.text = customerName
//        tvProductOption.text = productName
        tvDeliveryCost.text = deliveryCost.toString()
        if (totalPayment != 0) tvTotalPrice.text = totalPayment.toString()
        else tvTotalPrice.visibility = View.GONE
        tvTotalGross.text = totalGross.toString()
//        tvValuePrice.text = priceProduct.toString()


        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        dialog.setContentView(menuPopup)
        imgClose.setOnClickListener {
            dialog!!.dismiss()
        }
        dialog.setOnCancelListener {  }
        dialog.show()


    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun packagingOrderDetailsIncome(context: Context,position: Int,customerName : String,
                              productName : String, priceProduct : Int, storeName : String,
                              storeAddress : String, orderNumber : Int, orderDateTime : String,
                              deliveryCost : Int, totalPayment : Int,paymentMethod : String,
                              models : List<DataOrderItem?>?,totalGross: Int){
        sharedPref = SharedPreferences(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_packaging_order,null)
        val dialog = BottomSheetDialog(context)
        val tvEstimateTime = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvValuePrice = menuPopup.findViewById<TextView>(R.id.tv_value_price)
        val tvStoreName = menuPopup.findViewById<TextView>(R.id.tv_store_name)
        val tvStoreAddress = menuPopup.findViewById<TextView>(R.id.tv_store_address)
        val tvOrderNumber = menuPopup.findViewById<TextView>(R.id.tv_order_number)
        val tvOrderDateTime = menuPopup.findViewById<TextView>(R.id.tv_order_date_time)
        val tvCustomerName = menuPopup.findViewById<TextView>(R.id.tv_customer_name)
        val tvDeliverAddress = menuPopup.findViewById<TextView>(R.id.tv_address)
        val tvProductOption = menuPopup.findViewById<TextView>(R.id.tv_option)
        val tvProductPrice = menuPopup.findViewById<TextView>(R.id.tv_value_price)
        val tvDeliveryCost = menuPopup.findViewById<TextView>(R.id.value_delivery_cost)
        val tvTotalPrice = menuPopup.findViewById<TextView>(R.id.value_general_payment)
        val tvEstimate = menuPopup.findViewById<TextView>(R.id.tv_choice)
        val tvConfirm = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val tvPaymentMethod = menuPopup.findViewById<TextView>(R.id.tv_general_payment)
        val recyclerView = menuPopup.findViewById<RecyclerView>(R.id.recyclerview)
        val tvTotalGross = menuPopup.findViewById<TextView>(R.id.tv_sum_price)


        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val instant = Instant.parse(orderDateTime)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val dateTime = formatter.format(localDateTime)
        val estimateTime = sharedPref.getIntData(Constant.ESTIMATE_TIME)

        val layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        Log.d("DATA-PACKAGING", "packagingOrderDetailsIncome: $position")
        val adapter = IncomeProductNameAdapter(models!![position])

        recyclerView.adapter = adapter

        tvOrderDateTime.text = dateTime
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
        tvCustomerName.text = customerName
//        tvProductOption.text = productName
        tvDeliveryCost.text = deliveryCost.toString()
        if (totalPayment != 0) tvTotalPrice.text = totalPayment.toString()
        else tvTotalPrice.visibility = View.GONE
        tvTotalGross.text = totalGross.toString()
//        tvValuePrice.text = priceProduct.toString()


        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        dialog.setContentView(menuPopup)
        imgClose.setOnClickListener {
            dialog!!.dismiss()
        }
        dialog.setOnCancelListener {  }
        dialog.show()


    }

    fun popupOutOfStock(context: Context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_modify_stocks_beverage, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonSubmit = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        imgClose.setOnClickListener {
            popup.dismiss()
        }
        buttonSubmit.setOnClickListener {
            popup.dismiss()
        }
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)

    }

    fun notifPopupScanSubscribe(context : Context,productName : String,qrScanner : String,
                                qrScanViewModel : QrScanViewModel){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_qr_scan_subscribe, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonSubmit = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val buttonCancel = menuPopup.findViewById<TextView>(R.id.tv_no)
        val titleName = menuPopup.findViewById<TextView>(R.id.tvTittlePopup)
        val imageOut = menuPopup.findViewById<ConstraintLayout>(R.id.conts_1)
        val homeActivity = HomeActivity()
        popup.setBackgroundDrawable(null)
        HomeActivity.isCouponOrVoucherClaimedShown = false


        imageOut.setOnClickListener {
            popup.dismiss()
            HomeActivity.isCouponOrVoucherClaimedShown = false
        }
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)

        imgClose.setOnClickListener {
            popup.dismiss()
            HomeActivity.isCouponOrVoucherClaimedShown = false
        }
        buttonCancel.setOnClickListener {
            popup.dismiss()
            HomeActivity.isCouponOrVoucherClaimedShown = false

        }
        buttonSubmit.setOnClickListener {
            popup.dismiss()
            qrScanViewModel.qrScannerCode(codeQr = qrScanner)
            HomeActivity.isCouponOrVoucherClaimedShown = true
        }

        titleName.text = "[$productName] 횟수를 1회 차감합니다."
    }

    fun notifPopupScanCoupon(context : Context,productName : String,imageName: String,
                                     priceProduct: Int,qrScanner: String,qrScanViewModel : QrScanViewModel){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_qr_scan_coupon, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonSubmit = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val titleName = menuPopup.findViewById<TextView>(R.id.tvTittlePopup)
        val priceProductName = menuPopup.findViewById<TextView>(R.id.tv_price_popup)
        val imageProduct = menuPopup.findViewById<CircleImageView>(R.id.img_circle)
        val imageOut = menuPopup.findViewById<ConstraintLayout>(R.id.conts_1)
        HomeActivity.isCouponOrVoucherClaimedShown = false


        popup.setBackgroundDrawable(null)

        imageOut.setOnClickListener {
            popup.dismiss()
            HomeActivity.isCouponOrVoucherClaimedShown = false
        }
        imgClose.setOnClickListener {
            popup.dismiss()
            HomeActivity.isCouponOrVoucherClaimedShown = false
        }
        buttonSubmit.setOnClickListener {
            popup.dismiss()
            qrScanViewModel.qrScannerCode(codeQr = qrScanner)
            HomeActivity.isCouponOrVoucherClaimedShown = true
        }
        titleName.text = "기프티콘 $productName"
        priceProductName.text = priceProduct.toString()
        Glide.with(context).load(imageName).into(imageProduct)
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)

    }

    fun notifPopupScanVoucher(context : Context,productName : String,imageName: String,
                                      totalAmount: Int,qrScanner: String,
                              qrScanViewModel : QrScanViewModel, popupCallback: PopupCallback){
        sharedPref = SharedPreferences(context)
        HomeActivity.isCouponOrVoucherClaimedShown = false
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_qr_scan_voucher, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonSubmit = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val titleName = menuPopup.findViewById<TextView>(R.id.tvTittlePopup)
        val priceProductName = menuPopup.findViewById<TextView>(R.id.tv_harga_popup)
        val imageProduct = menuPopup.findViewById<CircleImageView>(R.id.img_circle)
        val edtAmount = menuPopup.findViewById<EditText>(R.id.edt_amount)
        var amountTotal = 0
        val imageOut = menuPopup.findViewById<ConstraintLayout>(R.id.conts_1)

        popup.setBackgroundDrawable(null)

        imageOut.setOnClickListener {
            popup.dismiss()
            HomeActivity.isCouponOrVoucherClaimedShown = false
        }

        imgClose.setOnClickListener {
            popup.dismiss()
            HomeActivity.isCouponOrVoucherClaimedShown = false
        }
        buttonSubmit.setOnClickListener {
            val inputText = edtAmount.text.toString().trim()
            if (inputText.isNotEmpty()) {
                try {
                    amountTotal = inputText.toInt()
                    if (amountTotal > totalAmount){
                        popupCallback.showToast("Total Amount Cannot Be More Than Voucher Amount")
                    }else{
                        qrScanViewModel.qrScannerCode(codeQr = qrScanner, totalAmount = amountTotal)
                        popup.dismiss()
                        HomeActivity.isCouponOrVoucherClaimedShown = true
                    }
                } catch (e: NumberFormatException) {
                    popupCallback.showToast("잘못된 입력 입니다. 올바른 숫자를 입력해주세요.")
                }
            } else {
                popupCallback.showToast("전체 금액을 입력해주세요.")
            }
        }
        titleName.text = productName
        priceProductName.text = totalAmount.toString()
        Glide.with(context).load(imageName).into(imageProduct)
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)
    }

    fun sideMenuPopup(context: Context, activity: Activity,popupCallback: PopupCallback,
                      settingViewModel: SettingViewModel,isOpen : Boolean, barcode : ActivityResultLauncher<ScanOptions>
    ) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.left_side_menu, null)
        val popup = PopupWindow(
            menuPopup,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        )
        val imgOut = menuPopup.findViewById<ImageView>(R.id.img_popup2)
        val btnStocks = menuPopup.findViewById<TextView>(R.id.tv_inventory_management)
        val btnNotice = menuPopup.findViewById<TextView>(R.id.tv_notice)
        val btnNewOrder = menuPopup.findViewById<TextView>(R.id.tv_new_order)
        val btnOngoingOrder = menuPopup.findViewById<TextView>(R.id.tv_progress_order)
        val btnCompleteOrder = menuPopup.findViewById<TextView>(R.id.tv_complete_order)
        val btnIncomeHome = menuPopup.findViewById<TextView>(R.id.tv_inquiry_call)
        val btnLogout = menuPopup.findViewById<TextView>(R.id.tv_logout)
        val btnScan = menuPopup.findViewById<ConstraintLayout>(R.id.btn_scan)
        val btnHome = menuPopup.findViewById<TextView>(R.id.tv_processing_order)
        val btnIncomeOrder = menuPopup.findViewById<TextView>(R.id.tv_income_order)
        val btnIncomeCoupon = menuPopup.findViewById<TextView>(R.id.tv_income_by_coupon)
        val btnIncomeSubscribe = menuPopup.findViewById<TextView>(R.id.tv_income_by_subscribe)
        val btnIncomeVoucher = menuPopup.findViewById<TextView>(R.id.tv_income_by_voucher)
        tvStoreStatus = menuPopup.findViewById(R.id.tv_store_open_close)
        switchStatusStore = menuPopup.findViewById(R.id.swOnOff)

        // Set touch interceptor to dismiss the popup when touched outside
        imgOut.setOnClickListener {
            popup.dismiss()
        }
        btnHome.setOnClickListener {
            context.startActivity(Intent(context, HomeActivity::class.java))
            activity.finish()
        }
        btnScan.setOnClickListener {
            val options: ScanOptions = ScanOptions().setOrientationLocked(false).setCaptureActivity(
                ScanQrActivity::class.java
            )
            barcode.launch(options)
        }
        btnLogout.setOnClickListener {
            context.startActivity(Intent(context, LoginActivity::class.java))
            activity.finish()
            popup.dismiss()
        }

        btnStocks.setOnClickListener {
            context.startActivity(Intent(context, StocksActivity::class.java))
            activity.finish()
            popup.dismiss()
        }
        btnNotice.setOnClickListener {
            context.startActivity(Intent(context, NoticeActivity::class.java))
            activity.finish()
            popup.dismiss()
        }
        btnIncomeOrder.setOnClickListener {
            context.startActivity(Intent(context, OrderIncomeActivity::class.java))
            activity.finish()
            popup.dismiss()
        }
        btnIncomeHome.setOnClickListener {
            context.startActivity(Intent(context, OrderIncomeActivity::class.java))
            activity.finish()
            popup.dismiss()
        }
        btnIncomeSubscribe.setOnClickListener {
            context.startActivity(Intent(context, IncomeSubscribeActivity::class.java))
            activity.finish()
            popup.dismiss()
        }
        btnIncomeCoupon.setOnClickListener {
            context.startActivity(Intent(context, IncomeCouponActivity::class.java))
            activity.finish()
            popup.dismiss()
        }
        btnIncomeVoucher.setOnClickListener {
            context.startActivity(Intent(context, IncomeActivity::class.java))
            activity.finish()
            popup.dismiss()
        }
        btnNewOrder.setOnClickListener {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra("ORDER-CATEGORY",100)
            context.startActivity(intent)
            activity.finish()
            popup.dismiss()
        }
        btnOngoingOrder.setOnClickListener {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra("ORDER-CATEGORY",200)
            context.startActivity(intent)
            activity.finish()
            popup.dismiss()
        }
        btnCompleteOrder.setOnClickListener {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra("ORDER-CATEGORY",300)
            context.startActivity(intent)
            activity.finish()
            popup.dismiss()
        }

        if (isOpen){
            tvStoreStatus.text = "영업 시작"
        }else{
            tvStoreStatus.text = "영업 종료"
        }

        switchStatusStore.isChecked = isOpen
        Log.d("IS-CHECKED", "sideMenuPopup: $isOpen")

        switchStatusStore.setOnClickListener {
            Log.d("KLIK-SWITCH", "sideMenuPopup: ${switchStatusStore.isChecked}")
            if (switchStatusStore.isChecked){
                popupOpenStore(context,settingViewModel)
            }else{
                popupCloseStore(context, settingViewModel)
            }
        }
        // Set an empty background to make the popup dismiss when touched outside
        popup.setBackgroundDrawable(null)
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)
    }


    fun popupOpenStore(context: Context,settingViewModel: SettingViewModel){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_qr_scan_subscribe, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonSubmit = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val buttonCancel = menuPopup.findViewById<TextView>(R.id.tv_no)
        val titleName = menuPopup.findViewById<TextView>(R.id.tvTittlePopup)
        val imageOut = menuPopup.findViewById<ConstraintLayout>(R.id.conts_1)

        popup.setBackgroundDrawable(null)

        imageOut.setOnClickListener {
            popup.dismiss()
        }

        imgClose.setOnClickListener {
            popup.dismiss()
        }
        buttonSubmit.setOnClickListener {
            popup.dismiss()
//            tvStoreStatus.text = "영업 시작"
            settingViewModel.sendUpdateStatus(2)
        }
        buttonCancel.setOnClickListener {
            switchStatusStore.isChecked = false
            popup.dismiss()
        }
        titleName.text = "영업 시작 상태로 변경하시겠습니까?"
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)
    }

    fun popupCloseStore(context: Context,settingViewModel: SettingViewModel){
        sharedPref = SharedPreferences(context)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_temp_or_close, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonClose = menuPopup.findViewById<TextView>(R.id.tv_close)
        val buttonTemp = menuPopup.findViewById<TextView>(R.id.tv_temp)
        val titleName = menuPopup.findViewById<TextView>(R.id.tvTittlePopup)
        val imageOut = menuPopup.findViewById<ConstraintLayout>(R.id.conts_1)

        popup.setBackgroundDrawable(null)

        imageOut.setOnClickListener {
            switchStatusStore.isChecked = true
            popup.dismiss()
        }

        imgClose.setOnClickListener {
            switchStatusStore.isChecked = true
            popup.dismiss()
        }
        buttonClose.setOnClickListener {
//            tvStoreStatus.text = "영업 종료"
            settingViewModel.sendUpdateStatus(0)
            popup.dismiss()
        }
        buttonTemp.setOnClickListener {
            popup.dismiss()
            settingViewModel.sendUpdateStatus(1)
        }
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)
    }



    interface PopupCallback {
        fun showToast(message: String)
    }

    override fun onResume() {
        super.onResume()
    }

    fun updateStoreStatus(isOpen: Boolean) {
        tvStoreStatus.text = if (isOpen) "영업 시작" else "영업 종료"
    }



}
