package com.arturmaslov.skubaware.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.arturmaslov.skubaware.App
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.source.MainRepository
import com.arturmaslov.skubaware.data.source.remote.LoadStatus
import com.arturmaslov.skubaware.data.usecase.UpdateLocalWithRemoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MainVM(
    private val mainRepo: MainRepository,
    app: Application,
    private val updateLocalWithRemoteUseCase: UpdateLocalWithRemoteUseCase
) : BaseVM(mainRepo, app) {

    private var initialProductList: List<Product?>? = emptyList()
    private val startProductList = MutableStateFlow<List<Product?>?>(emptyList())
    private val finalProductList = MutableStateFlow<List<Product?>?>(emptyList())
    private val productSortOption = MutableStateFlow(ProductSortOption.BRAND)

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
                val localProducts = mainRepo.getLocalProducts()
                // show local data without internet
                val internetIsAvailable = internetIsAvailable().value
                val localDataExists = !localProducts.isNullOrEmpty()
                if (!internetIsAvailable && localDataExists) {
                    initialProductList = localProducts
                    startProductList.value = localProducts
                } else {
                    val productList = updateLocalWithRemoteUseCase.execute(localProducts)
                    initialProductList = productList
                    startProductList.value = productList
                }
                sortProductLists(productSortOption.value)
                setLoadStatus(LoadStatus.DONE)
            } catch (e: Exception) {
                setLoadStatus(LoadStatus.ERROR)
                Timber.e(e.localizedMessage!!)
            }
        }
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
                sortProductLists(productSortOption.value)
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

        sortProductLists(productSortOption.value)
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

        sortProductLists(productSortOption.value)
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