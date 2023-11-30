package com.arturmaslov.skubaware.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.helpers.utils.Constants
import com.arturmaslov.skubaware.viewmodel.ProductFilterOption
import com.arturmaslov.skubaware.viewmodel.ProductSortOption

@Composable
fun MainLayout(
    initialProductList: List<Product?>,
    startProductList: List<Product?>,
    finalProductList: List<Product?>,
    onStartClick: (Product) -> Unit,
    onFinalClick: (Product) -> Unit,
    onFabClick: () -> Unit,
    isSortFilterDialogVisible: Pair<Boolean, Boolean>,
    onSortOptionChanged: (ProductSortOption) -> Unit,
    onSortDialogDismiss: () -> Unit,
    onFilterDialogDismiss: () -> Unit,
    currentSortOption: ProductSortOption,
    onRangeFilterOptionChanged: (ProductFilterOption, Float, Float) -> Unit,
    onDropdownFilterOptionChanged: (ProductFilterOption, String) -> Unit
) {
    val context = LocalContext.current

    val configuration = context.resources.configuration
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val screenWidthInDp = configuration.screenWidthDp.dp
    val isWideEnough = screenWidthInDp >= 600.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            if (isPortrait && !isWideEnough) {
                PortraitLayoutWithTabs(
                    startProductList = startProductList,
                    finalProductList = finalProductList,
                    onStartClick = onStartClick,
                    onFinalClick = onFinalClick
                )
            } else {
                LandscapeLayoutSideBySide(
                    startProductList = startProductList,
                    finalProductList = finalProductList,
                    onStartClick = onStartClick,
                    onFinalClick = onFinalClick
                )
            }
        }
        FloatingActionButton(
            onClick = onFabClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_check_24),
                contentDescription = Constants.EMPTY_STRING
            )
        }
        if (isSortFilterDialogVisible.first) {
            SortDialog(
                onSortOptionSelected = { onSortOptionChanged(it) },
                onDismiss = onSortDialogDismiss,
                currentSortOption = currentSortOption
            )
        }
        if (isSortFilterDialogVisible.second) {
            FilterDialog(
                onRangeFilterOptionSelected = { option, from, to ->
                    onRangeFilterOptionChanged(option, from, to)
                },
                onDropdownFilterOptionSelected = { option, optionName ->
                    onDropdownFilterOptionChanged(option, optionName)
                },
                onDismiss = onFilterDialogDismiss,
                initialProductList = initialProductList
            )
        }
    }
}