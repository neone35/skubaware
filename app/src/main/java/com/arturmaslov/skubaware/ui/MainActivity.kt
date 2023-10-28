package com.arturmaslov.skubaware.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.LiveData
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.source.remote.LoadStatus
import com.arturmaslov.skubaware.ui.compose.LoadingScreen
import com.arturmaslov.skubaware.ui.compose.MainLayout
import com.arturmaslov.skubaware.ui.compose.SkubaTopAppBar
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme
import com.arturmaslov.skubaware.utils.HelperUtils
import com.arturmaslov.skubaware.utils.ToastUtils
import com.arturmaslov.skubaware.utils.UiHelper
import com.arturmaslov.skubaware.viewmodel.MainVM
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : ComponentActivity(), UiHelper {

    private val mainVM: MainVM by viewModel()

    private var disableBackCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setObservers()
        setListeners()


        setContent {
            val internetAvailable = mainVM.internetIsAvailable().observeAsState().value
            val loadStatus = mainVM.loadStatus().observeAsState().value
            val initialProductList =
                mainVM.initialProductList().observeAsState().value ?: emptyList()
            val finalProductList = mainVM.finalProductList().observeAsState().value ?: emptyList()

            internetAvailable?.let {
                if (!it) ToastUtils.updateLong(this, getString(R.string.no_internet))
            }

            SkubaWareTheme {
                Scaffold(
                    topBar = {
                        SkubaTopAppBar(
                            onFilterClick = {
                                ToastUtils.updateShort(this, "Filters clicked")
                            }
                        )
                    },
                    content = { it ->
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
                                MainLayout(
                                    initialProductList = initialProductList,
                                    finalProductList = finalProductList,
                                    onInitialClick = { product ->
                                        mainVM.transferToFinalList(
                                            product
                                        )
                                    },
                                    onFinalClick = { product ->
                                        mainVM.transferToInitialList(
                                            product
                                        )
                                    },
                                    onFabClick = {
                                        if (finalProductList.isNotEmpty()) {
                                            generateAndOpenJsonFile(finalProductList)
                                        } else {
                                            ToastUtils.updateShort(
                                                this,
                                                getString(R.string.no_products_added)
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    override fun setObservers() {
        observeLoadStatus(mainVM.loadStatus())
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
        if (!repoResponseLD.hasObservers()) {
            repoResponseLD.observe(this) {
                Timber.i("observeRepositoryResponse: $it")
            }
        }
    }

    private fun generateAndOpenJsonFile(dataList: List<Product?>) {
        // Create a JSON file
        val filename = "output_data_${HelperUtils.getCurrentDateTime()}.json"
        val jsonUri = HelperUtils.storePlainTextFileInMediaStore(dataList, filename)

        // Open the JSON file with an external app
        jsonUri.let {
            val openIntent = Intent(Intent.ACTION_VIEW)
            openIntent.setDataAndType(it, contentResolver.getType(it))
            openIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivity(openIntent)
        }
    }
}