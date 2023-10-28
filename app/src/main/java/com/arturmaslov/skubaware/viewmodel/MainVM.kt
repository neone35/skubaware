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
                if (internetIsAvailable().value == false && !localProducts.isNullOrEmpty()) {
                    initialProductList = localProducts
                    startProductList.value = localProducts
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

    fun filterProductLists(
        by: ProductFilterOption,
        from: String,
        to: String
    ) {
        Timber.i("Running HomeVM filterProductLists with $by")
        viewModelScope.launch {
            setLoadStatus(LoadStatus.LOADING)
            try {
                startProductList.value = initialProductList?.filter { product ->
                    when (by) {
                        ProductFilterOption.SKN -> {
                            val noLetterSkn = product?.skn?.replace(Regex("[A-Za-z]"), "")
                            val sknInt = noLetterSkn?.toFloat() ?: 0f
                            val fromInt = from.toFloat()
                            val toInt = to.toFloat()
                            sknInt in fromInt..toInt
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
                        ProductSortOption.SKN -> it?.skn
                        ProductSortOption.NAME -> it?.name
                        ProductSortOption.BRAND -> it?.brand
                        ProductSortOption.BUYER_CODE -> it?.buyerCode
                        ProductSortOption.QUANTITY -> it?.quantity
                    }
                }
                finalProductList.value = finalProductList.value?.sortedBy {
                    when (by) {
                        ProductSortOption.SKN -> it?.skn
                        ProductSortOption.NAME -> it?.name
                        ProductSortOption.BRAND -> it?.brand
                        ProductSortOption.BUYER_CODE -> it?.buyerCode
                        ProductSortOption.QUANTITY -> it?.quantity
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