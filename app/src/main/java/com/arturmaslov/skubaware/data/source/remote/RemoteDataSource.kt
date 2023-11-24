package com.arturmaslov.skubaware.data.source.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.models.ProductDto
import com.arturmaslov.skubaware.data.models.toDomainModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Call
import timber.log.Timber

class RemoteDataSource(
    private val api: Api,
    private val mDispatcher: CoroutineDispatcher
) : RemoteData {

    // watched from main thread for toast messages
    private val _remoteResponse = MutableLiveData<String?>()
    override val remoteResponse: LiveData<String?> get() = _remoteResponse

    private suspend fun <T : Any> checkCallAndReturn(call: Call<T>, funcName: String): T? =
        withContext(mDispatcher) {
            Timber.i("Running checkCallAndReturn()")
            var resultData: T? = null
            when (val result = api.getResult(call)) {
                is Result.Success -> {
                    Timber.d("Success: remote data retrieved")
                    resultData = result.data
                }

                is Result.NetworkFailure -> _remoteResponse.postValue(result.error.toString())
                is Result.ApiFailure -> _remoteResponse.postValue(result.errorString)
                is Result.Loading -> Timber.d("$funcName is loading")
            }
            return@withContext resultData
        }

    override suspend fun fetchProductResponse() =
        withContext(mDispatcher) {
            Timber.i("Running fetchProductResponse()")
            val liveData = MutableLiveData<List<Product>?>()
            val call = api.apiService.fetchProductResponse()
            val name = object {}.javaClass.enclosingMethod?.name
            val resultData: List<ProductDto>? = checkCallAndReturn(call, name!!)
            val domainList = resultData?.map { it.toDomainModel() }
            liveData.postValue(domainList)
            liveData.apply { postValue(domainList) }
        }

}

interface RemoteData {
    val remoteResponse: LiveData<String?>
    suspend fun fetchProductResponse(): MutableLiveData<List<Product>?>
}