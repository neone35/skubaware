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
    productList: List<Product?>
) {
    val tabs = listOf(
        stringResource(R.string.first_tab_title),
        stringResource(R.string.second_tab_title)
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
        0 -> ProductList(productList = productList)
        1 -> ProductList(productList = productList)
    }
}