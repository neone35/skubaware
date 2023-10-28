package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product

@Composable
fun PortraitLayoutWithTabs(
    startProductList: List<Product?>,
    finalProductList: List<Product?>,
    onStartClick: (Product) -> Unit,
    onFinalClick: (Product) -> Unit
) {
    val tabs = listOf(
        stringResource(R.string.initial_title),
        stringResource(R.string.final_title)
    )
    var tabIndex by remember { mutableIntStateOf(0) }
    TabRow(selectedTabIndex = tabIndex) {
        tabs.forEachIndexed { index, tabTitle ->
            Tab(
                modifier = Modifier.height(40.dp),
                selected = tabIndex == index,
                onClick = { tabIndex = index }
            ) {
                Text(text = tabTitle)
            }
        }
    }
    when (tabIndex) {
        0 -> ProductList(
            productList = startProductList,
            onClick = onStartClick,
            endIconId = R.drawable.ic_add_24
        )

        1 -> ProductList(
            productList = finalProductList,
            onClick = onFinalClick,
            endIconId = R.drawable.ic_remove_24
        )
    }
}