package com.arturmaslov.skubaware.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arturmaslov.skubaware.data.models.Product

@Composable
fun MainLayout(
    initialProductList: List<Product?>,
    finalProductList: List<Product?>,
    onInitialClick: (Product) -> Unit,
    onFinalClick: (Product) -> Unit
) {
    val context = LocalContext.current

    val configuration = context.resources.configuration
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val screenWidthInDp = configuration.screenWidthDp.dp
    val isWideEnough = screenWidthInDp >= 600.dp

    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        if (isPortrait && !isWideEnough) {
            PortraitLayoutWithTabs(
                initialProductList = initialProductList,
                finalProductList = finalProductList,
                onInitialClick = onInitialClick,
                onFinalClick = onFinalClick
            )
        } else {
            LandscapeLayoutSideBySide(
                initialProductList = initialProductList,
                finalProductList = finalProductList,
                onInitialClick = onInitialClick,
                onFinalClick = onFinalClick
            )
        }
    }
}