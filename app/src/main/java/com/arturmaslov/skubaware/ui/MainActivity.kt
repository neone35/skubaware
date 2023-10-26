package com.arturmaslov.skubaware.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.source.remote.LoadStatus
import com.arturmaslov.skubaware.ui.compose.Greeting
import com.arturmaslov.skubaware.ui.compose.LoadingScreen
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme
import com.arturmaslov.skubaware.utils.UiHelper
import com.arturmaslov.skubaware.viewmodel.MainVM
import com.arturmaslov.tgnba.utils.ToastUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : ComponentActivity(), UiHelper {

    private val mainVM: MainVM by viewModel()

    private var disableBackCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        setListeners()
    }

    private fun showLoading(showLoading: Boolean) {
        Timber.d("showing loading is $showLoading")
        setContent {
            SkubaWareTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoadingScreen(showLoading = showLoading) {
                        Greeting("Android")
                    }
                }
            }
        }
    }

    override fun setObservers() {
        observeInternetAvailability()
        observeLoadStatus(mainVM.extLoadStatus)
        observeRepositoryResponse(mainVM.remoteResponse)
    }

    override fun setListeners() {
        // Impossible to go back for this activity and all its children if loading when enabled
        disableBackCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                return
            }
        }
        onBackPressedDispatcher.addCallback(this, disableBackCallback as OnBackPressedCallback)
    }

    private fun observeInternetAvailability() {
        mainVM.extInternetAvailable.observe(this) { intAvailable ->
            Timber.d("Internet is $intAvailable")
            if (intAvailable == false) {
                ToastUtils.updateLong(this, getString(R.string.no_internet))
                showLoading(true)
            }
        }
    }

    private fun observeLoadStatus(statusLD: LiveData<LoadStatus>) {
        statusLD.observe(this) {
            Timber.d("api status is $it")
            when (it) {
                LoadStatus.LOADING -> {
                    showLoading(true)
                    disableBackCallback?.isEnabled = true
                }

                LoadStatus.DONE -> {
                    showLoading(false)
                    disableBackCallback?.isEnabled = false
                }

                LoadStatus.ERROR -> {
                    showLoading(false)
                    Timber.e("Failure: $it")
                    disableBackCallback?.isEnabled = false
                }

                else -> {
                    showLoading(false)
                    disableBackCallback?.isEnabled = false
                }
            }
        }
    }

    private fun observeRepositoryResponse(repoResponseLD: LiveData<String?>) {
        println("observeRepositoryResponse called")
        if (!repoResponseLD.hasObservers()) {
            repoResponseLD.observe(this) {
                Timber.i("observeRepositoryResponse: $it")
                ToastUtils.updateShort(this, "$it")
            }
        }
    }
}