package com.arturmaslov.skubaware.data.usecase

import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.source.MainRepository
import timber.log.Timber

class UpdateLocalWithRemoteUseCase(
    private val mainRepo: MainRepository
) {
    suspend fun execute(localProducts: List<Product>?): List<Product>? {
        val remoteProducts = mainRepo.fetchProductResponse()
        // do not update local DB if remote data is the same
        return if (!listsEqual(localProducts, remoteProducts)) {
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

    private fun listsEqual(local: List<Product?>?, remote: List<Product>?): Boolean {
        if (local!!.size != remote!!.size) {
            return false
        }
        local.forEachIndexed { index, localValue ->
            val valueWithNullId = localValue?.copy(id = null)
            if (remote[index] != valueWithNullId) {
                Timber.d("Comparing ${remote[index]} with ${local[index]}")
                return false
            }
        }
        return true
    }
}