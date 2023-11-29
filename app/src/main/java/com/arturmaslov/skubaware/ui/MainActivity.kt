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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.data.source.remote.LoadStatus
import com.arturmaslov.skubaware.helpers.utils.HelperUtils
import com.arturmaslov.skubaware.helpers.utils.ToastUtils
import com.arturmaslov.skubaware.helpers.utils.UiHelper
import com.arturmaslov.skubaware.ui.compose.LoadingScreen
import com.arturmaslov.skubaware.ui.compose.MainLayout
import com.arturmaslov.skubaware.ui.compose.SkubaTopAppBar
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme
import com.arturmaslov.skubaware.viewmodel.MainVM
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
            val loadStatus = mainVM.loadStatus().collectAsState().value

            val initialProductList = mainVM.initialProductList() ?: emptyList()
            val startProductList =
                mainVM.startProductList().collectAsState().value ?: emptyList()
            val finalProductList = mainVM.finalProductList().collectAsState().value ?: emptyList()

            var isFilterSortDialogVisible by remember { mutableStateOf(false) }
            val productSortOption = mainVM.productSortOption().collectAsState().value

            SkubaWareTheme {
                Scaffold(
                    topBar = {
                        SkubaTopAppBar(onFilterClick = { isFilterSortDialogVisible = true })
                    },
                    content = {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            LoadingScreen(
                                showLoading = loadStatus == LoadStatus.LOADING
                            ) {
                                MainLayout(
                                    initialProductList = initialProductList,
                                    startProductList = startProductList,
                                    finalProductList = finalProductList,
                                    onStartClick = { product -> mainVM.transferToFinalList(product) },
                                    onFinalClick = { product -> mainVM.transferToStartList(product) },
                                    onFabClick = {
                                        if (finalProductList.isNotEmpty()) {
                                            generateAndOpenJsonFile(finalProductList)
                                        } else {
                                            ToastUtils.updateShort(
                                                this,
                                                getString(R.string.no_products_added)
                                            )
                                        }
                                    },
                                    isFilterSortDialogVisible = isFilterSortDialogVisible,
                                    onSortOptionChanged = { productSortOption ->
                                        mainVM.sortProductLists(productSortOption)
                                    },
                                    onFilterSortDialogDismiss = {
                                        isFilterSortDialogVisible = false
                                    },
                                    currentSortOption = productSortOption,
                                    onFilterOptionChanged = { option, from, to ->
                                        mainVM.filterProductLists(
                                            by = option,
                                            from = from,
                                            to = to
                                        )
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
        lifecycleScope.launch { observeInternetAvailability(mainVM.internetIsAvailable()) }
        lifecycleScope.launch { observeLoadStatusToDisableBack(mainVM.loadStatus()) }
        lifecycleScope.launch { observeRepositoryResponse(mainVM.remoteResponse) }
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

    private suspend fun observeInternetAvailability(statusFlow: StateFlow<Boolean>) {
        statusFlow.collect {
            if (!it) ToastUtils.updateLong(this, getString(R.string.no_internet))
        }
    }

    private suspend fun observeLoadStatusToDisableBack(statusFlow: StateFlow<LoadStatus>) {
        statusFlow.collect {
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
            }
        }
    }

    private suspend fun observeRepositoryResponse(repoResponseFlow: SharedFlow<String?>) {
        repoResponseFlow.collect {
            Timber.i("observeRepositoryResponse: $it")
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