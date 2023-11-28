package com.arturmaslov.skubaware.data.source.remote

import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.models.ProductDto
import com.arturmaslov.skubaware.data.models.toDomainModel
import com.arturmaslov.skubaware.helpers.extensions.BehaviorFlow
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import retrofit2.Call
import timber.log.Timber

class RemoteDataSource(
    private val api: Api,
    private val mDispatcher: CoroutineDispatcher
) : RemoteData {

    // watched from main thread for toast messages
    override val remoteResponse = BehaviorFlow<String?>()

    private suspend fun <T : Any> checkCallAndReturn(call: Call<T>, funcName: String): T? =
        withContext(mDispatcher) {
            Timber.i("Running checkCallAndReturn()")
            var resultData: T? = null
            when (val result = api.getResult(call)) {
                is Result.Success -> {
                    remoteResponse.tryEmit("Success: remote data retrieved")
                    resultData = result.data
                }

                is Result.NetworkFailure -> remoteResponse.tryEmit(result.error.toString())
                is Result.ApiFailure -> remoteResponse.tryEmit(result.errorString)
                is Result.Loading -> Timber.d("$funcName is loading")
            }
            return@withContext resultData
        }

    override suspend fun fetchProductResponse(): List<Product>? {
        Timber.i("Running fetchProductResponse()")
        val call = api.apiService.fetchProductResponse()
        val name = object {}.javaClass.enclosingMethod?.name
        val resultData: List<ProductDto>? = checkCallAndReturn(call, name!!)
        return resultData?.map { it.toDomainModel() }
    }

}

interface RemoteData {
    val remoteResponse: MutableSharedFlow<String?>
    suspend fun fetchProductResponse(): List<Product>?
}