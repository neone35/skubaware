package com.arturmaslov.skubaware.ui

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.source.remote.LoadStatus
import com.arturmaslov.skubaware.ui.compose.LandscapeLayoutSideBySide
import com.arturmaslov.skubaware.ui.compose.LoadingScreen
import com.arturmaslov.skubaware.ui.compose.PortraitLayoutWithTabs
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme
import com.arturmaslov.skubaware.utils.UiHelper
import com.arturmaslov.skubaware.viewmodel.MainVM
import com.arturmaslov.tgnba.utils.ToastUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : ComponentActivity(), UiHelper {

    private val mainVM: MainVM by viewModel()

    private var disableBackCallback: OnBackPressedCallback? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        setListeners()

        setContent {
            val internetAvailable = mainVM.extInternetAvailable.observeAsState().value
            val loadStatus = mainVM.extLoadStatus.observeAsState().value
            val productList = mainVM.extProductList.observeAsState().value ?: emptyList()

            internetAvailable?.let {
                if (!it) ToastUtils.updateLong(this, getString(R.string.no_internet))
            }


            SkubaWareTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(stringResource(R.string.app_name)) }
                        )
                    },
                    content = {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            LoadingScreen(
                                showLoading =
                                loadStatus == LoadStatus.LOADING || internetAvailable == false
                            ) {
                                val context = LocalContext.current
                                val isPortrait =
                                    context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    if (isPortrait) {
                                        PortraitLayoutWithTabs(productList = productList)
                                    } else {
                                        LandscapeLayoutSideBySide(productList = productList)
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    override fun setObservers() {
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

    private fun observeLoadStatus(statusLD: LiveData<LoadStatus>) {
        statusLD.observe(this) {
            Timber.d("api status is $it")
            when (it) {
                LoadStatus.LOADING -> {
                    disableBackCallback?.isEnabled = true
                }

                LoadStatus.DONE -> {
                    disableBackCallback?.isEnabled = false
                }

                LoadStatus.ERROR -> {
                    Timber.e("Failure: $it")
                    disableBackCallback?.isEnabled = false
                }

                else -> {
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
            }
        }
    }
}