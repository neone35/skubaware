@file:Suppress("unused")

package com.arturmaslov.skubaware.helpers.extensions

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

fun <T> BehaviorFlow() = MutableSharedFlow<T>(
    replay = 1,
    extraBufferCapacity = 0,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

fun <T> PublishFlow() = MutableSharedFlow<T>(
    replay = 0,
    extraBufferCapacity = 1,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)

fun <T> BehaviorFlow(initialValue: T) = BehaviorFlow<T>().also { it.tryEmit(initialValue) }

fun <T> MutableSharedFlow<T>.requireValue(): T =
    replayCache.firstOrNull() ?: error("required flow value missing")

val <T> MutableSharedFlow<T>.value: T?
    get() = replayCache.firstOrNull()

fun IntervalFlow(intervalMillis: Long, initialDelayMillis: Long = 0): Flow<Unit> {
    return flow {
        delay(initialDelayMillis)
        while (true) {
            emit(Unit)
            delay(intervalMillis)
        }
    }
}

suspend fun <T> retryUntilSuccess(
    delayOnErrorMillis: Long? = null,
    action: suspend () -> T
): T {
    return coroutineScope {
        var result: T? = null
        do {
            runCatching { action.invoke() }
                .onSuccess { result = it }
                .onFailure { throwable ->
                    delayOnErrorMillis?.let { delay(it) }
                    Timber.e(throwable)
                }
        } while (result == null)
        result!!
    }
}