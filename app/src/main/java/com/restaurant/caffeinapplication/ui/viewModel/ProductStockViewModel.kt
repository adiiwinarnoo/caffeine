package com.restaurant.caffeinapplication.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.caffeinapplication.data.model.ResponseOutStock
import com.restaurant.caffeinapplication.data.model.ResponseProductStocks
import com.restaurant.caffeinapplication.data.model.UpdateOutStock
import com.restaurant.caffeinapplication.data.repository.ProductStocksRepository

class ProductStockViewModel : ViewModel() {
    var repoProductStock = ProductStocksRepository()
    var dataProductStock = MutableLiveData<ResponseProductStocks>()
    var updateDataOutStock = MutableLiveData<ResponseOutStock>()
    var deleteDataOutStock = MutableLiveData<UpdateOutStock>()
    var dataCategoryProduct = MutableLiveData<ResponseProductStocks>()

    fun getProductStocks() : MutableLiveData<ResponseProductStocks>{
        repoProductStock.getProductStocks {
            dataProductStock.postValue(it)
        }
        return dataProductStock
    }
    fun getProductStocksByCategorySearch(idCategoryProduct : Int? = null, searchText : String? = null) : MutableLiveData<ResponseProductStocks>{
        repoProductStock.getProductStocksByCategorySearch(idCategoryProduct,searchText) {
            dataCategoryProduct.postValue(it)
        }
        return dataCategoryProduct
    }
    fun updateOutStock(idProduct : Int) : MutableLiveData<ResponseOutStock>{
        repoProductStock.updateOutStock(idProduct){
            updateDataOutStock.postValue(it)
        }
        return updateDataOutStock
    }
    fun deleteOutStock(idProduct : Int) : MutableLiveData<UpdateOutStock>{
        repoProductStock.deleteOutStock(idProduct){
            deleteDataOutStock.postValue(it)
        }
        return deleteDataOutStock
    }

}