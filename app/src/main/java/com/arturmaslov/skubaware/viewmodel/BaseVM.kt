package com.arturmaslov.skubaware.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturmaslov.skubaware.data.source.MainRepository
import com.arturmaslov.skubaware.data.source.remote.LoadStatus
import com.arturmaslov.skubaware.helpers.utils.NetworkChecker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

open class BaseVM(
    mainRepo: MainRepository,
    private val app: Application
) : ViewModel() {

    val remoteResponse = mainRepo.remoteResponse as SharedFlow<String?>

    private val internetIsAvailable = MutableStateFlow(true)
    private val loadStatus = MutableStateFlow(LoadStatus.LOADING)

    init {
        // runs every time VM is created (not view created)
        viewModelScope.launch {
            loadStatus.emit(LoadStatus.LOADING)
            internetIsAvailable.value = NetworkChecker(app).isNetworkConnected()
            loadStatus.emit(LoadStatus.DONE)
        }
    }

    fun setLoadStatus(status: LoadStatus) {
        viewModelScope.launch {
            Timber.i("Running BaseVM setBaseStatus with $status")
            loadStatus.emit(status)
        }
    }

    fun internetIsAvailable() = internetIsAvailable
    fun loadStatus() = loadStatus

}