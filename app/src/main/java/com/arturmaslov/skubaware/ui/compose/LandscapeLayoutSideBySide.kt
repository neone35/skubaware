package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product

@Composable
fun LandscapeLayoutSideBySide(
    startProductList: List<Product?>,
    finalProductList: List<Product?>,
    onStartClick: (Product) -> Unit,
    onFinalClick: (Product) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
        verticalAlignment = Alignment.Top
    ) {
        ProductList(
            modifier = Modifier.weight(1.0f),
            productList = startProductList,
            onClick = onStartClick,
            endIconId = R.drawable.ic_add_24
        )
        ProductList(
            modifier = Modifier.weight(1.0f),
            productList = finalProductList,
            onClick = onFinalClick,
            endIconId = R.drawable.ic_remove_24
        )
    }
}