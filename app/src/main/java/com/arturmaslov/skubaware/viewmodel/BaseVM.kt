package com.arturmaslov.skubaware.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arturmaslov.skubaware.data.source.MainRepository
import com.arturmaslov.skubaware.data.source.remote.LoadStatus
import com.arturmaslov.skubaware.utils.NetworkChecker
import kotlinx.coroutines.launch
import timber.log.Timber

open class BaseVM(
    private val mainRepo: MainRepository,
    private val app: Application
) : ViewModel() {

    val remoteResponse: LiveData<String?> get() = mainRepo.remoteResponse

    private val internetIsAvailable = MutableLiveData(false)
    private val loadStatus = MutableLiveData<LoadStatus>()

    init {
        // runs every time VM is created (not view created)
        viewModelScope.launch {
            loadStatus.value = LoadStatus.LOADING
            internetIsAvailable.value = NetworkChecker(app).isNetworkConnected()
            loadStatus.value = LoadStatus.DONE
        }
    }

    fun setLoadStatus(status: LoadStatus) {
        Timber.i("Running BaseVM setBaseStatus with $status")
        loadStatus.value = status
    }

    fun internetIsAvailable() = internetIsAvailable
    fun loadStatus() = loadStatus

}