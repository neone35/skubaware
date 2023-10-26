package com.arturmaslov.skubaware.data.source.remote

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class NetworkFailure(val error: Throwable?) : Result<Nothing>()
    data class ApiFailure(val errorString: String?) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "$data"
            is NetworkFailure -> "$error"
            is ApiFailure -> "$errorString"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if Result is of type Success & holds non-null Success.data.
 */
val Result<*>.succeeded
    get() = this is Result.Success && data != null