package com.arturmaslov.skubaware.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.arturmaslov.skubaware.App
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.source.MainRepository
import com.arturmaslov.skubaware.data.source.remote.LoadStatus
import kotlinx.coroutines.launch
import timber.log.Timber

class MainVM(
    private val mainRepo: MainRepository,
    app: Application
) : BaseVM(mainRepo, app) {

    private val initialProductList = MutableLiveData<List<Product?>?>(emptyList())
    private val finalProductList = MutableLiveData<List<Product?>?>(emptyList())
    private val productSortOption = MutableLiveData(ProductSortOption.BRAND)

    init {
        // runs every time VM is created (not view created)
        Timber.i("MainVM created!")
        setLocalProductList()
    }

    private fun setLocalProductList() {
        Timber.i("Running HomeVM updateLocalProductList")
        viewModelScope.launch {
            setLoadStatus(LoadStatus.LOADING)
            try {
                val localProducts = mainRepo.getLocalProducts().value
                // show local data without internet
                if (internetIsAvailable().value == false && !localProducts.isNullOrEmpty()) {
                    initialProductList.value = localProducts
                } else {
                    val remoteProducts = mainRepo.fetchProductResponse().value
                    // do not update local DB if remote data is the same
                    if (!isEqual(localProducts, remoteProducts)) {
                        val rowIds: MutableList<Int> = mutableListOf()
                        mainRepo.deleteProducts()
                        remoteProducts?.forEach {
                            it.let { product -> mainRepo.insertProduct(product) }
                                ?.let { rowId -> rowIds.add(rowId.toInt()) }
                        }
                        Timber.d("$rowIds ids inserted into database")
                        initialProductList.value = mainRepo.getLocalProducts().value
                    } else {
                        Timber.i("MainVM productList local=remote")
                        initialProductList.value = mainRepo.getLocalProducts().value
                    }
                }
                filterSortProductLists(productSortOption.value!!)
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

    fun filterSortProductLists(by: ProductSortOption) {
        Timber.i("Running HomeVM sortProductList with $by")
        viewModelScope.launch {
            setLoadStatus(LoadStatus.LOADING)
            try {
                initialProductList.value = initialProductList.value?.sortedBy {
                    when (by) {
                        ProductSortOption.SKN -> it?.skn
                        ProductSortOption.NAME -> it?.name
                        ProductSortOption.BRAND -> it?.brand
                        ProductSortOption.BUYER_CODE -> it?.buyerCode
                    }
                }
                finalProductList.value = finalProductList.value?.sortedBy {
                    when (by) {
                        ProductSortOption.SKN -> it?.skn
                        ProductSortOption.NAME -> it?.name
                        ProductSortOption.BRAND -> it?.brand
                        ProductSortOption.BUYER_CODE -> it?.buyerCode
                    }
                }
                productSortOption.value = by
                setLoadStatus(LoadStatus.DONE)
            } catch (e: Exception) {
                setLoadStatus(LoadStatus.ERROR)
                Timber.e(e.localizedMessage!!)
            }
        }
    }

    fun transferToFinalList(product: Product) {
        // remove from initial
        val tempInitialProductList = initialProductList.value?.toMutableList()
        initialProductList.value = tempInitialProductList
            ?.filter { tempProduct ->
                tempProduct?.id != product.id
            }

        // add to final
        val tempFinalProductList = finalProductList.value?.toMutableList()
        tempFinalProductList?.add(product)
        finalProductList.value = tempFinalProductList

        filterSortProductLists(productSortOption.value!!)
    }

    fun transferToInitialList(product: Product) {
        // remove from final
        val tempFinalProductList = finalProductList.value?.toMutableList()
        finalProductList.value = tempFinalProductList
            ?.filter { tempProduct ->
                tempProduct?.id != product.id
            }

        // add to initial
        val tempInitialProductList = initialProductList.value?.toMutableList()
        tempInitialProductList?.add(product)
        initialProductList.value = tempInitialProductList

        filterSortProductLists(productSortOption.value!!)
    }

    fun initialProductList() = initialProductList
    fun finalProductList() = finalProductList
    fun productSortOption() = productSortOption

}

enum class ProductSortOption(val sortOption: String) {
    SKN(App.getAppContext().getString(R.string.skn)),
    BRAND(App.getAppContext().getString(R.string.brand)),
    NAME(App.getAppContext().getString(R.string.name)),
    BUYER_CODE(App.getAppContext().getString(R.string.buyer_code));
}