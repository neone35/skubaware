package com.arturmaslov.skubaware.data.source.remote

import com.arturmaslov.skubaware.data.models.ApiError
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class LoadStatus { LOADING, ERROR, DONE }

class Api(val apiService: ApiService) {

    init {
        Timber.d("Api initialized")
    }

    // checks remote response result before sending to repository
    suspend fun <T : Any> getResult(call: Call<T>): Result<T> = suspendCoroutine {
        call.enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, error: Throwable) {
                Timber.e("network error: $error")
                it.resume(Result.NetworkFailure(error))
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                response.body()?.run { it.resume(Result.Success(this)) }
                response.errorBody()?.run {
                    val apiError = Gson().fromJson(this.string(), ApiError::class.java)
                    val errorString = "${apiError.statusCode} - ${apiError.message}"
                    Timber.e("api error: $errorString")
                    it.resume(Result.ApiFailure(errorString))
                    //it.resume(Failure(HttpException(response)))
                }
            }
        })
    }
}