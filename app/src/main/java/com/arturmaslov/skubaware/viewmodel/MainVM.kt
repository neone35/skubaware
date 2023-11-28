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

    private var initialProductList: List<Product?>? = emptyList()
    private val startProductList = MutableLiveData<List<Product?>?>(emptyList())
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
                val internetIsAvailable = internetIsAvailable().value
                val localDataExists = !localProducts.isNullOrEmpty()
                if (!internetIsAvailable && localDataExists) {
                    initialProductList = localProducts
                    startProductList.value = localProducts
                } else {
                    val remoteProducts = mainRepo.fetchProductResponse().value
                    // do not update local DB if remote data is the same
                    if (!listsEqual(localProducts, remoteProducts)) {
                        val rowIds: MutableList<Int> = mutableListOf()
                        mainRepo.deleteProducts()
                        remoteProducts?.forEach {
                            it.let { product -> mainRepo.insertProduct(product) }
                                ?.let { rowId -> rowIds.add(rowId.toInt()) }
                        }
                        Timber.d("$rowIds ids inserted into database")
                        initialProductList = mainRepo.getLocalProducts().value
                        startProductList.value = mainRepo.getLocalProducts().value
                    } else {
                        Timber.i("MainVM productList local==remote")
                        initialProductList = localProducts
                        startProductList.value = localProducts
                    }
                }
                sortProductLists(productSortOption.value!!)
                setLoadStatus(LoadStatus.DONE)
            } catch (e: Exception) {
                setLoadStatus(LoadStatus.ERROR)
                Timber.e(e.localizedMessage!!)
            }
        }
    }

    private fun listsEqual(local: List<Product?>?, remote: List<Product>?): Boolean {
        if (local!!.size != remote!!.size) {
            return false
        }
        local.forEachIndexed { index, value ->
            val valueWithNullId = value?.copy(id = null)
            if (remote[index] != valueWithNullId) {
                Timber.d("Comparing ${remote[index]} with ${local[index]}")
                return false
            }
        }
        return true
    }

    fun filterProductLists(
        by: ProductFilterOption,
        from: Float,
        to: Float
    ) {
        Timber.i("Running HomeVM filterProductLists with $by")
        viewModelScope.launch {
            setLoadStatus(LoadStatus.LOADING)
            try {
                startProductList.value = initialProductList?.filter { product ->
                    when (by) {
                        ProductFilterOption.SKN -> {
                            val skn = product?.skn?.toFloat() ?: 0f
                            skn in from..to
                        }

                        else -> {
                            false
                        }
                    }
                }
                sortProductLists(productSortOption.value!!)
                setLoadStatus(LoadStatus.DONE)
            } catch (e: Exception) {
                setLoadStatus(LoadStatus.ERROR)
                Timber.e(e.localizedMessage!!)
            }
        }
    }

    fun sortProductLists(by: ProductSortOption) {
        Timber.i("Running HomeVM sortProductLists with $by")
        viewModelScope.launch {
            setLoadStatus(LoadStatus.LOADING)
            try {
                startProductList.value = startProductList.value?.sortedBy {
                    when (by) {
                        ProductSortOption.SKN -> it?.skn.toString()
                        ProductSortOption.NAME -> it?.name.toString()
                        ProductSortOption.BRAND -> it?.brand.toString()
                        ProductSortOption.BUYER_CODE -> it?.buyerCode.toString()
                        ProductSortOption.QUANTITY -> it?.quantity.toString()
                    }
                }
                finalProductList.value = finalProductList.value?.sortedBy {
                    when (by) {
                        ProductSortOption.SKN -> it?.skn.toString()
                        ProductSortOption.NAME -> it?.name.toString()
                        ProductSortOption.BRAND -> it?.brand.toString()
                        ProductSortOption.BUYER_CODE -> it?.buyerCode.toString()
                        ProductSortOption.QUANTITY -> it?.quantity.toString()
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
        // remove from start
        val tempStartProductList = startProductList.value?.toMutableList()
        startProductList.value = tempStartProductList
            ?.filter { tempProduct ->
                tempProduct?.id != product.id
            }

        // add to final
        val tempFinalProductList = finalProductList.value?.toMutableList()
        tempFinalProductList?.add(product)
        finalProductList.value = tempFinalProductList

        sortProductLists(productSortOption.value!!)
    }

    fun transferToStartList(product: Product) {
        // remove from final
        val tempFinalProductList = finalProductList.value?.toMutableList()
        finalProductList.value = tempFinalProductList
            ?.filter { tempProduct ->
                tempProduct?.id != product.id
            }

        // add to start
        val tempStartProductList = startProductList.value?.toMutableList()
        tempStartProductList?.add(product)
        startProductList.value = tempStartProductList

        sortProductLists(productSortOption.value!!)
    }

    fun initialProductList() = initialProductList
    fun startProductList() = startProductList
    fun finalProductList() = finalProductList
    fun productSortOption() = productSortOption

}

enum class ProductSortOption(val sortOption: String) {
    SKN(App.getAppContext().getString(R.string.skn)),
    BRAND(App.getAppContext().getString(R.string.brand)),
    NAME(App.getAppContext().getString(R.string.name)),
    BUYER_CODE(App.getAppContext().getString(R.string.buyer_code)),
    QUANTITY(App.getAppContext().getString(R.string.quantity));
}

enum class ProductFilterOption(val filterOption: String) {
    SKN(App.getAppContext().getString(R.string.skn)),
    BRAND(App.getAppContext().getString(R.string.brand)),
    NAME(App.getAppContext().getString(R.string.name)),
    BUYER_CODE(App.getAppContext().getString(R.string.buyer_code)),
    QUANTITY(App.getAppContext().getString(R.string.quantity));
}