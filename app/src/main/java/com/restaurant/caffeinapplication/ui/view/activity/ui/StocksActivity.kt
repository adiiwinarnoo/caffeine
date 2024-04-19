package com.restaurant.caffeinapplication.ui.view.activity.ui

import Popup
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.restaurant.caffeinapplication.R
import com.restaurant.caffeinapplication.adapter.StockProductAdapter
import com.restaurant.caffeinapplication.adapter.TestAdapter
import com.restaurant.caffeinapplication.data.model.StockProduct
import com.restaurant.caffeinapplication.data.model.TestModel
import com.restaurant.caffeinapplication.data.network.FirebaseMessageService
import com.restaurant.caffeinapplication.databinding.ActivityStocksBinding
import com.restaurant.caffeinapplication.ui.view.activity.*
import com.restaurant.caffeinapplication.ui.viewModel.CategoryStocksViewModel
import com.restaurant.caffeinapplication.ui.viewModel.ProductStockViewModel
import com.restaurant.caffeinapplication.ui.viewModel.QrScanViewModel
import com.restaurant.caffeinapplication.ui.viewModel.SettingViewModel
import com.restaurant.caffeinapplication.utils.Constant
import com.restaurant.caffeinapplication.utils.ScanContract
import com.restaurant.caffeinapplication.utils.SharedPreferences
import com.google.zxing.client.android.Intents

class StocksActivity : AppCompatActivity(), Popup.PopupCallback {

    lateinit var binding : ActivityStocksBinding
    lateinit var adapterCategory : TestAdapter
    lateinit var adapterProduct : StockProductAdapter
    lateinit var viewModelProductStock : ProductStockViewModel
    lateinit var viewModelCategoryStocks : CategoryStocksViewModel
    var model = ArrayList<TestModel>()
    var modelStock = ArrayList<StockProduct>()
    val popup = Popup()
    var stringSearch = ""
    var categoryId = -1
    lateinit var sharedPref : SharedPreferences
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var qrScanViewModel : QrScanViewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStocksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelProductStock = ViewModelProvider(this)[ProductStockViewModel::class.java]
        viewModelCategoryStocks = ViewModelProvider(this)[CategoryStocksViewModel::class.java]
        settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        qrScanViewModel = ViewModelProvider(this)[QrScanViewModel::class.java]
        sharedPref = SharedPreferences(this)

        var sideMenu = findViewById<ConstraintLayout>(R.id.btn_side_menu)
        binding.progressbar.visibility = View.VISIBLE

        sideMenu.setOnClickListener {
            popup.sideMenuPopup(this,this,this,settingViewModel,
                sharedPref.getBooleanData(Constant.IS_OPEN),barcodeLauncher)
        }
        getProductStocks()
        getCategoryStocks()

        binding.edtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                stringSearch = p0.toString()
                if (stringSearch.isNullOrEmpty() || stringSearch.equals("") && categoryId != -1){
                    viewModelProductStock.getProductStocksByCategorySearch(idCategoryProduct = categoryId)
                }else if (!stringSearch.isNullOrEmpty()){
                    viewModelProductStock.getProductStocksByCategorySearch(searchText = stringSearch)
                }else{
                    viewModelProductStock.getProductStocksByCategorySearch(categoryId,stringSearch)
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.recyclerCategory.layoutManager = GridLayoutManager(this,5)
        binding.recyclerProduct.layoutManager = GridLayoutManager(this,3)

        viewModelProductStock.dataProductStock.observe(this){
            when(it.message){
                "SUCCESS" ->{
                    binding.progressbar.visibility = View.GONE
                    adapterProduct = StockProductAdapter(it.data!!.result!!)
                    binding.recyclerProduct.adapter = adapterProduct
                    adapterProduct.setItemClickListener(object : StockProductAdapter.ItemClickListener{
                        override fun onClickUpdateStock(view: View, position: Int, idProduct: Int) {
                            popupUpdateStock(this@StocksActivity,idProduct)
                        }
                    })
                    adapterProduct.setItemClickListener(object : StockProductAdapter.ItemClickListener2{
                        override fun onClickOutStock(view: View, position: Int, idProduct: Int) {
                            popupDeleteStock(this@StocksActivity,idProduct)
                        }
                    })
                }
                "Token Times Up" -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(this, "Token Times Up", Toast.LENGTH_SHORT).show()
                }
                "you dont have access!" ->{
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(this, "you dont have access!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModelCategoryStocks.dataCategoryStocks.observe(this){
            when(it.message){
                "SUCCESS" -> {
                    binding.progressbar.visibility = View.GONE
                    adapterCategory = TestAdapter(it.data!!.result)
                    binding.recyclerCategory.adapter = adapterCategory
                    adapterCategory.setItemClickListener(object : TestAdapter.ItemClickListener{
                        override fun onClickCategory(view: View, position: Int, idCategoryProduct: Int) {
                            categoryId = idCategoryProduct
                            if (stringSearch.equals("") || stringSearch.isNullOrEmpty()){
                                viewModelProductStock.getProductStocksByCategorySearch(idCategoryProduct = categoryId)
                            }else if (!!stringSearch.isNullOrEmpty()){
                                viewModelProductStock.getProductStocksByCategorySearch(searchText = stringSearch)
                            }else{
                                viewModelProductStock.getProductStocksByCategorySearch(categoryId,stringSearch)
                            }
                        }
                    })
                    adapterCategory.setItemClickListener(object : TestAdapter.ItemClickListener2{
                        override fun onClickCategoryDefault(view: View, position: Int) {
                            binding.edtSearch.setText(null)
                            viewModelProductStock.getProductStocks()
                        }

                    })
                    viewModelProductStock.dataCategoryProduct.observe(this){
                        when(it.message){
                            "SUCCESS" -> {
                                binding.progressbar.visibility = View.GONE
                                adapterProduct = StockProductAdapter(it.data!!.result!!)
                                binding.recyclerProduct.adapter = adapterProduct
                            }
                            "Token Times Up" -> {
                                binding.progressbar.visibility = View.GONE
                                Toast.makeText(this, "Token Times Up", Toast.LENGTH_SHORT).show()
                            }
                            "you dont have access!" ->{
                                binding.progressbar.visibility = View.GONE
                                Toast.makeText(this, "you dont have access!", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
                "Token Times Up" -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(this, "Token Times Up", Toast.LENGTH_SHORT).show()
                }
                "you dont have access!" ->{
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(this, "you dont have access!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        FirebaseMessageService.AppStatus.appIsOpen = true
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

    private fun getProductStocks(){
        viewModelProductStock.getProductStocks()
    }
    private fun getCategoryStocks(){
        viewModelCategoryStocks.getCategoryStocks()
    }

    fun popupUpdateStock(context: Context,idProduct : Int){
        var viewModel = ViewModelProvider(this)[ProductStockViewModel::class.java]
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_modify_stocks_beverage, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonSubmit = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        imgClose.setOnClickListener {
            popup.dismiss()
        }
        buttonSubmit.setOnClickListener {
            viewModel.deleteOutStock(idProduct)
        }
        viewModel.deleteDataOutStock.observe(this){
            when(it.message){
                "SUCCESS DELETE" -> {
                    popup.dismiss()
                    getProductStocks()
                }
                "Token Times Up" -> {
                    Toast.makeText(this, "Token Times Up", Toast.LENGTH_SHORT).show()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)

    }
    fun popupDeleteStock(context: Context,idProduct : Int){
        var viewModel = ViewModelProvider(this)[ProductStockViewModel::class.java]
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val menuPopup = inflater!!.inflate(R.layout.popup_sold_out_stocks, null)
        val popup = PopupWindow(menuPopup, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
        val imgClose = menuPopup.findViewById<TextView>(R.id.img_close)
        val buttonSubmit = menuPopup.findViewById<TextView>(R.id.tv_confirm)
        val buttonCancel = menuPopup.findViewById<TextView>(R.id.tv_no)
        imgClose.setOnClickListener {
            popup.dismiss()
        }
        buttonCancel.setOnClickListener {
            popup.dismiss()
        }
        buttonSubmit.setOnClickListener {
            viewModel.updateOutStock(idProduct)
        }
        viewModel.updateDataOutStock.observe(this){
            when(it.message){
                "SUCCESS CREATE" ->{
                    popup.dismiss()
                    getProductStocks()
                }
                "Token Times Up" -> {
                    Toast.makeText(this, "Token Times Up", Toast.LENGTH_SHORT).show()
                }
                "you dont have access!" ->{
                    Toast.makeText(this, "you dont have access!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        popup.showAtLocation(menuPopup, Gravity.START, 0, 0)

    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun qrScanner(codeQr:String){
        qrScanViewModel.qrScanPreview(codeQr)
    }
}