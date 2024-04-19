package com.restaurant.caffeinapplication.ui.view.activity.ui

import Popup
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.adapter.IncomeSubscribeAdapter
import com.restaurant.caffeinapplication.data.model.*
import com.restaurant.caffeinapplication.data.network.FirebaseMessageService
import com.restaurant.caffeinapplication.databinding.ActivityIncomeSubscribeBinding
import com.restaurant.caffeinapplication.ui.view.activity.*
import com.restaurant.caffeinapplication.ui.viewModel.IncomeViewModel
import com.restaurant.caffeinapplication.ui.viewModel.QrScanViewModel
import com.restaurant.caffeinapplication.ui.viewModel.SettingViewModel
import com.restaurant.caffeinapplication.utils.Constant
import com.restaurant.caffeinapplication.utils.ScanContract
import com.restaurant.caffeinapplication.utils.SharedPreferences
import com.google.zxing.client.android.Intents
import java.time.LocalDateTime
import java.time.YearMonth

class IncomeSubscribeActivity : AppCompatActivity(),Popup.PopupCallback {

    lateinit var binding : ActivityIncomeSubscribeBinding
    lateinit var groups : MutableList<GroupIncomeSubscribe>
    lateinit var incomeViewModel : IncomeViewModel
    lateinit var yearKorean : List<String>
    val popup = Popup()
    lateinit var sharedPref : SharedPreferences
    private lateinit var settingViewModel: SettingViewModel

    var starYear = ""
    var starMonth = ""
    var starDate = ""
    var endYear = ""
    var endMonth = ""
    var endDate = ""
    private lateinit var qrScanViewModel : QrScanViewModel

    companion object {
        var isCouponOrVoucherClaimedShown = false
        var isToastShown = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeSubscribeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        incomeViewModel = ViewModelProvider(this)[IncomeViewModel::class.java]
        qrScanViewModel = ViewModelProvider(this)[QrScanViewModel::class.java]
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        sharedPref = SharedPreferences(this)

        var sideMenu = findViewById<ConstraintLayout>(R.id.btn_side_menu)
        sideMenu.setOnClickListener {
            popup.sideMenuPopup(this,this,this,settingViewModel,
                sharedPref.getBooleanData(Constant.IS_OPEN),barcodeLauncher)
        }

        groups = mutableListOf()
        getAllSubscribe()

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
        val monthPositionToday = currentMonth - 1
        val previousMonth = currentDateTime.minusMonths(1)
        val monthPosition = previousMonth.monthValue - 1 // Bulan dimulai dari 0 (Januari) hingga 11 (Desember)
        binding.spinnerStartdateMonth.setSelection(monthPosition)
        binding.spinnerEndateMonth.setSelection(monthPositionToday)

        // Mengatur spinner tanggal
        val previousYear = previousMonth.year
        val previousMonthValue = previousMonth.monthValue
        val daysInPreviousMonth = YearMonth.of(previousYear, previousMonthValue).lengthOfMonth()
        val previousDate = if (currentDate > daysInPreviousMonth) daysInPreviousMonth else currentDate
        val dayArray = (1..daysInPreviousMonth).toList().map { it.toString().padStart(2, '0') }
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dayArray)
        val currentDateFormatted = currentDate.toString().padStart(2, '0')
        val datePosition = dayArray.indexOf(currentDateFormatted)
        binding.spinnerStartdateDate.adapter = dayAdapter
        binding.spinnerEndateDate.adapter = dayAdapter
        binding.spinnerStartdateDate.setSelection(previousDate - 1)
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

        binding.btnReset.setOnClickListener {
            binding.progressbar.visibility = View.VISIBLE
            var starDateNow = "${starYear}-${starMonth}-${starDate}"
            var endDateNow = "${endYear}-${endMonth}-${endDate}"
            groups = mutableListOf()
            getAllSubscribe(starDate = starDateNow, endDate = endDateNow)
        }
        binding.btnCancel.setOnClickListener {
            binding.progressbar.visibility = View.VISIBLE
            getAllSubscribe()
        }

        incomeViewModel.dataIncomeSubscribe.observe(this){
            when (it.message){
                "SUCCESS" -> {
                    binding.progressbar.visibility = View.GONE
                    //set year by korean text
                    yearKorean = it.response?.mapNotNull { responseItemVoucher ->
                        responseItemVoucher?.year?.let { year ->
                            "$year 년"
                        }
                    } ?: emptyList()

                    for (i in it.response!!.indices) {
                        for (j in it.response[i]!!.month!!.indices) {
                            val year = it.response[i]?.year.toString()
                            val month = it.response[i]!!.month!![j]!!.month?.toString()
                            val incomeYear = it.response[i]?.month!![j]?.totalIncome

                            val dateList = mutableListOf<String>()
                            val incomeDateList = mutableListOf<String>()
                            val recyclerValueList = mutableListOf<List<DataSubscriptionItem>?>() // List to store recycler values

                            for (k in it.response[i]!!.month!![j]!!.date!!.indices) {
                                val date = it.response[i]!!.month!![j]!!.date!![k]!!.date?.toString()
                                val dateIncome = it.response[i]!!.month!![j]!!.date!![k]!!.totalIncome?.toString()
                                if (date != null) {
                                    dateList.add(date)
                                    incomeDateList.add(dateIncome!!)
                                }
                                recyclerValueList.add(it.response[i]!!.month!![j]!!.date!![k]!!.dataSubscription as List<DataSubscriptionItem>?)
                            }

                            if (year != null && month != null && dateList.isNotEmpty()) {
                                val modifiedDateList = dateList.map { "$it 일" }
                                val incomeListModif = incomeDateList.map { "₩ $it" }

                                val currentGroupTitle = "$year 년, $month 월"
                                val totalIncomeYearValue = "₩ $incomeYear"
                                val recyclerViewData = mutableListOf<ChildItemIncomeSubscribe>()

                                for ((index, modifiedDate) in modifiedDateList.withIndex()) {
                                    recyclerViewData.add(ChildItemIncomeSubscribe(modifiedDate, recyclerValueList[index]))
                                }

                                val group = GroupIncomeSubscribe(currentGroupTitle,totalIncomeYearValue, modifiedDateList,incomeListModif, recyclerViewData, false)
                                groups.add(group)
                            }
                        }
                    }

                    val adapter = IncomeSubscribeAdapter(this, groups)
                    binding.expandableListView.setAdapter(adapter)

                    binding.expandableListView.setOnGroupExpandListener { groupPosition ->
                        groups[groupPosition].isExpanded = true
                        adapter.notifyDataSetChanged()
                    }

                    binding.expandableListView.setOnGroupCollapseListener { groupPosition ->
                        groups[groupPosition].isExpanded = false
                        adapter.notifyDataSetChanged()
                    }

                    adapter.setItemClickListener(object : IncomeSubscribeAdapter.ItemClickListener {
                        override fun onClick(view: View, position: Int, groupPosition: Int) {
                            val flatPosition = binding.expandableListView.getFlatListPosition(
                                ExpandableListView.getPackedPositionForChild(groupPosition, position)
                            )
                            val localPosition = binding.expandableListView.getFlatListPosition(
                                ExpandableListView.getPackedPositionForChild(groupPosition, position)
                            )
                            val localVisibleRect = Rect()
                            binding.expandableListView.getLocalVisibleRect(localVisibleRect)
                            if (!localVisibleRect.contains(localPosition, 1)) {
                                binding.expandableListView.smoothScrollToPosition(flatPosition)
                            }
                        }
                    })


                }
            }
        }

        qrScanViewModel.dataQrModel.observe(this){
            var resultItem = it.messageData?.data
            if (it.messageData!!.type.equals("subcriptions",ignoreCase = true)){
                IncomeCouponActivity.isToastShown = false
                popup.notifPopupScanSubscribe(this,it.messageData.data?.name!!,it.messageData.codeQr!!,
                    qrScanViewModel)
            }else if (it.messageData.type.equals("coupon",ignoreCase = true)){
                IncomeCouponActivity.isToastShown = false
                popup.notifPopupScanCoupon(this,resultItem?.product?.productName!!,
                    resultItem.product?.thumbnailProduct!!,resultItem.product?.priceProduct!!,
                    it.messageData.codeQr!!,qrScanViewModel)
            }else if (it.messageData.type.equals("voucher",ignoreCase = true)){
                IncomeCouponActivity.isToastShown = false
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
            Log.d("PESAN-QR", "onResume-coupon: ${isCouponOrVoucherClaimedShown}")
            when (it.message){
                "SUCCESS" -> {
                    getAllSubscribe()
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

    private fun getAllSubscribe(starDate:String? = null, endDate: String? = null){
        incomeViewModel.getAllSubscribeViewModel(starDate = starDate, endDate = endDate)
    }

    private fun qrScanner(codeQr:String){
        qrScanViewModel.qrScanPreview(codeQr)
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

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}