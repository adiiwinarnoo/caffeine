package com.restaurant.caffeinapplication.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.restaurant.caffeinapplication.data.model.ResponseCategoryStocks
import com.restaurant.caffeinapplication.data.repository.CategoryStocksRepository

class CategoryStocksViewModel : ViewModel() {

    var repoCategoryStocks = CategoryStocksRepository()
    var dataCategoryStocks = MutableLiveData<ResponseCategoryStocks>()

    fun getCategoryStocks() : MutableLiveData<ResponseCategoryStocks>{
        repoCategoryStocks.getCategoryStocks {
            dataCategoryStocks.postValue(it)
        }
        return dataCategoryStocks
    }

}