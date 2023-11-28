package com.arturmaslov.skubaware.data.source

import androidx.lifecycle.MutableLiveData
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.source.local.LocalData
import com.arturmaslov.skubaware.data.source.local.LocalDataSource
import com.arturmaslov.skubaware.data.source.remote.RemoteData
import com.arturmaslov.skubaware.data.source.remote.RemoteDataSource
import timber.log.Timber

class MainRepository(
    private val mLocalDataSource: LocalDataSource,
    private val mRemoteDataSource: RemoteDataSource
) : LocalData, RemoteData {

    // watched from main thread for toast messages
    override val remoteResponse = mRemoteDataSource.remoteResponse

    init {
        Timber.d("Injection MainRepository")
    }

    override suspend fun getLocalProducts(): MutableLiveData<List<Product>?> {
        return mLocalDataSource.getLocalProducts()
    }

    override suspend fun deleteProducts() {
        return mLocalDataSource.deleteProducts()
    }

    override suspend fun insertProduct(product: Product): Long? {
        return mLocalDataSource.insertProduct(product)
    }

    override suspend fun fetchProductResponse(): MutableLiveData<List<Product>?> {
        return mRemoteDataSource.fetchProductResponse()
    }

}