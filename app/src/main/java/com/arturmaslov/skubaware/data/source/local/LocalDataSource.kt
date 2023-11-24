package com.arturmaslov.skubaware.data.source.local

import androidx.lifecycle.MutableLiveData
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.models.toDomainModel
import com.arturmaslov.skubaware.data.models.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

class LocalDataSource(
    mLocalDatabase: LocalDatabase,
    private val mDispatcher: CoroutineDispatcher
) : LocalData {

    private val productDao: ProductDao? = mLocalDatabase.productDao

    // check for local data on startup and use it before making any remote requests
    override suspend fun getLocalProducts() =
        withContext(mDispatcher) {
            Timber.i("Running getLocalProducts()")
            val liveData = MutableLiveData<List<Product>?>()
            val localProducts = productDao?.getProducts()
            val domainList = localProducts?.map { it.toDomainModel() }
            if (localProducts != null) {
                liveData.postValue(domainList)
                Timber.i("Success: local products $localProducts retrieved")
            } else {
                Timber.i("Failure: unable to retrieve local products")
            }
            liveData.apply { postValue(domainList) }
        }


    override suspend fun deleteProducts() =
        withContext(mDispatcher) {
            Timber.i("Running deleteProducts()")
            val deletedRows = productDao?.deleteProducts()!!
            if (deletedRows != 0) {
                Timber.i("Success: all local product data deleted")
            } else {
                Timber.i("Failure: unable to delete local product data")
            }
        }

    override suspend fun insertProduct(product: Product): Long? =
        withContext(mDispatcher) {
            Timber.i("Running insertProduct()")
            val insertedId = productDao?.insertProduct(product.toEntity())
            if (insertedId != null) {
                Timber.i("Success: product with id ${product.name} inserted")
            } else {
                Timber.i("Failure: unable to delete local product")
            }
            return@withContext insertedId
        }

}

interface LocalData {
    suspend fun getLocalProducts(): MutableLiveData<List<Product>?>
    suspend fun deleteProducts()
    suspend fun insertProduct(product: Product): Long?
}