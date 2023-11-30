package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme
import com.arturmaslov.skubaware.viewmodel.ProductFilterOption

@Preview(showBackground = true)
@Composable
fun FilterDialogPreview() {
    SkubaWareTheme {
        FilterDialog(
            onDismiss = {},
            onFilterOptionSelected = { _, _, _ -> },
            initialProductList =
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
            )
        )
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onFilterOptionSelected: (ProductFilterOption, Float, Float) -> Unit,
    initialProductList: List<Product?>
) {
    val productFilterOptionList = ProductFilterOption.values().toList()

    Dialog(
        onDismissRequest = { onDismiss() }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
            ) {
                Text(
                    text = stringResource(R.string.filter_by),
                    style = MaterialTheme.typography.titleSmall
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                ) {
                    productFilterOptionList.forEach { option ->
                        FilterOptionItem(
                            option,
                            onFilterOptionSelected,
                            initialProductList
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterOptionItem(
    option: ProductFilterOption,
    onFilterSelected: (ProductFilterOption, Float, Float) -> Unit,
    initialProductList: List<Product?>
) {
    var initialMinValue = 0f
    var initialMaxValue = 0f

    when (option) {
        ProductFilterOption.SKN -> {
            initialMinValue = initialProductList
                .minOfOrNull { it?.skn?.toFloat() ?: 0.0f }
                ?: 0.0f
            initialMaxValue = initialProductList
                .maxOfOrNull { it?.skn?.toFloat() ?: 0.0f }
                ?: 0.0f
        }

        ProductFilterOption.BUYER_CODE -> {
            initialMinValue = initialProductList
                .minOfOrNull { it?.buyerCode?.toFloat() ?: 0.0f }
                ?: 0.0f
            initialMaxValue = initialProductList
                .maxOfOrNull { it?.buyerCode?.toFloat() ?: 0.0f }
                ?: 0.0f
        }

        ProductFilterOption.QUANTITY -> {
            initialMinValue = initialProductList
                .minOfOrNull { it?.quantity?.toFloat() ?: 0.0f }
                ?: 0.0f
            initialMaxValue = initialProductList
                .maxOfOrNull { it?.quantity?.toFloat() ?: 0.0f }
                ?: 0.0f
        }

        else -> {}
    }

    when (option) {
        ProductFilterOption.SKN, ProductFilterOption.BUYER_CODE, ProductFilterOption.QUANTITY -> {
            var slidingRange by remember {
                mutableStateOf(initialMinValue..initialMaxValue)
            }
            Text(
                text = option.filterOption,
                style = MaterialTheme.typography.labelMedium
            )
            RangeSlider(
                value = slidingRange,
                steps = 100,
                onValueChange = { range ->
                    slidingRange = range
                },
                valueRange = initialMinValue..initialMaxValue,
                onValueChangeFinished = {
                    onFilterSelected(
                        option,
                        slidingRange.start,
                        slidingRange.endInclusive
                    )
                },
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = slidingRange.start.toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = slidingRange.endInclusive.toInt().toString(),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        else -> {}
    }
}

