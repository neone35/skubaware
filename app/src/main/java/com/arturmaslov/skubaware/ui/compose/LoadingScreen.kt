package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme

@Preview(showBackground = true)
@Composable
fun LoadingPreview() {
    SkubaWareTheme {
        LoadingScreen(showLoading = true) {
            Greeting("Android")
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