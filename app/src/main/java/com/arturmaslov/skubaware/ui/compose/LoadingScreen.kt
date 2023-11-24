package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    SkubaWareTheme {
        LoadingScreen(showLoading = true) {
            ProductList(
                listOf(
                    Product(
                        imgUrl = null,
                        quantity = "1.25",
                        skn = 9552,
                        brand = "Skulabamba",
                        name = "padangeles",
                        buyerCode = 959595
                    ),
                    Product(
                        imgUrl = null,
                        quantity = "1.25",
                        skn = 9552,
                        brand = "Skulabamba",
                        name = "padangeles",
                        buyerCode = 959595
                    )
                ),
                onClick = {},
                endIconId = R.drawable.ic_add_24
            )
        }
    }
}

@Composable
fun LoadingScreen(
    showLoading: Boolean,
    content: @Composable () -> Unit
) {
    if (showLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        content()
    }
}