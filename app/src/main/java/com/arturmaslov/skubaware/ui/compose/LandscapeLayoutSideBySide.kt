package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arturmaslov.skubaware.data.models.Product

@Composable
fun LandscapeLayoutSideBySide(
    productList: List<Product?>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.Top
    ) {
        ProductList(
            modifier = Modifier.weight(1.0f),
            productList = productList
        )
        ProductList(
            modifier = Modifier.weight(1.0f),
            productList = productList
        )
    }
}