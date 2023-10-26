package com.arturmaslov.skubaware.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.source.MainRepository
import com.arturmaslov.skubaware.data.source.remote.LoadStatus
import kotlinx.coroutines.launch
import timber.log.Timber

class MainVM(
    private val mainRepo: MainRepository,
    app: Application
) : BaseVM(mainRepo, app) {

    private val _productList = MutableLiveData<List<Product?>?>()
    val extProductList: LiveData<List<Product?>?> get() = _productList
    private val _productSortOption = MutableLiveData<String?>()
    val extProductSortOption: LiveData<String?> get() = _productSortOption

    init {
        // runs every time VM is created (not view created)
        Timber.i("MainVM created!")
    }

    fun setLocalProductList() {
        Timber.i("Running HomeVM updateLocalProductList")
        viewModelScope.launch {
            setLoadStatus(LoadStatus.LOADING)
            try {
                val localProducts = mainRepo.getLocalProducts().value
                // show local data without internet
                if (extInternetAvailable.value == false && !localProducts.isNullOrEmpty()) {
                    _productList.value = localProducts
                    sortProductList(ProductSortOption.NAME) // default sort
                } else {
                    val remoteProducts = mainRepo.fetchProductResponse().value
                    // do not update local DB if remote data is the same
                    if (!isEqual(localProducts, remoteProducts)) {
                        val rowIds: MutableList<Int> = mutableListOf()
                        remoteProducts?.forEach {
                            it.let { product -> mainRepo.insertProduct(product) }
                                ?.let { rowId -> rowIds.add(rowId.toInt()) }
                        }
                        Timber.d("$rowIds ids inserted into database")
                        _productList.value = mainRepo.getLocalProducts().value
                    } else {
                        Timber.i("MainVM productList local=remote")
                        _productList.value = mainRepo.getLocalProducts().value
                        sortProductList(ProductSortOption.NAME) // default sort
                    }
                }
                setLoadStatus(LoadStatus.DONE)
            } catch (e: Exception) {
                setLoadStatus(LoadStatus.ERROR)
                Timber.e(e.localizedMessage!!)
            }
        }
    }

    private fun <T> isEqual(first: List<T>?, second: List<T>?): Boolean {
        if (first!!.size != second!!.size) {
            return false
        }
        first.forEachIndexed { index, value ->
            if (second[index] != value) {
                return false
            }
        }
        return true
    }

    fun sortProductList(by: ProductSortOption) {
        Timber.i("Running HomeVM sortProductList with $by")
        viewModelScope.launch {
            setLoadStatus(LoadStatus.LOADING)
            try {
                _productList.value = _productList.value?.sortedBy {
                    when (by) {
                        ProductSortOption.SKN -> it?.skn
                        ProductSortOption.NAME -> it?.name
                        ProductSortOption.BRAND -> it?.brand
                        ProductSortOption.BUYER_CODE -> it?.buyerCode.toString()
                    }
                }
                _productSortOption.value = by.sortOption
                setLoadStatus(LoadStatus.DONE)
            } catch (e: Exception) {
                setLoadStatus(LoadStatus.ERROR)
                Timber.e(e.localizedMessage!!)
            }
        }
    }

}

enum class ProductSortOption(val sortOption: String) {
    SKN("SKN"),
    BRAND("Brand"),
    NAME("Name"),
    BUYER_CODE("Buyer code");
}