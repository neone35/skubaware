package com.arturmaslov.skubaware.data.usecase

import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.source.MainRepository
import com.arturmaslov.skubaware.helpers.utils.HelperUtils
import timber.log.Timber

class UpdateLocalWithRemoteUseCase(
    private val mainRepo: MainRepository
) {
    suspend fun execute(localProducts: List<Product>?): List<Product>? {
        val remoteProducts = mainRepo.fetchProductResponse()
        // do not update local DB if remote data is the same
        val productListsAreEqual = HelperUtils.compareProductLists(localProducts, remoteProducts)
        return if (!productListsAreEqual) {
            val rowIds: MutableList<Int> = mutableListOf()
            mainRepo.deleteProducts()
            remoteProducts?.forEach {
                it.let { product -> mainRepo.insertProduct(product) }
                    ?.let { rowId -> rowIds.add(rowId.toInt()) }
            }
            Timber.d("$rowIds ids inserted into database")
            mainRepo.getLocalProducts()
        } else {
            Timber.i("UpdateLocalWithRemoteUseCase productList local==remote")
            localProducts
        }
    }
}