package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme
import com.arturmaslov.skubaware.utils.Constants
import com.arturmaslov.skubaware.viewmodel.ProductFilterOption
import com.arturmaslov.skubaware.viewmodel.ProductSortOption

@Preview(showBackground = true)
@Composable
fun FilterSortDialogPreview() {
    SkubaWareTheme {
        FilterSortDialog(
            onDismiss = {},
            onSortOptionSelected = {},
            currentSortOption = ProductSortOption.BRAND,
            onFilterOptionSelected = { _, _, _ -> },
            initialProductList = emptyList()
        )
    }
}

@Composable
fun FilterSortDialog(
    onDismiss: () -> Unit,
    onSortOptionSelected: (ProductSortOption) -> Unit,
    currentSortOption: ProductSortOption,
    onFilterOptionSelected: (ProductFilterOption, Float, Float) -> Unit,
    initialProductList: List<Product?>
) {
    val productSortOptionList = ProductSortOption.values().toList()
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
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                Text(
                    text = stringResource(R.string.sort_by),
                    style = MaterialTheme.typography.titleSmall
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(productSortOptionList) { index, option ->
                        SortOptionItem(option, onSortOptionSelected, currentSortOption)
                    }
                }
                Text(
                    text = stringResource(R.string.filter_by),
                    style = MaterialTheme.typography.titleSmall
                )
                LazyColumn {
                    itemsIndexed(productFilterOptionList) { index, option ->
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
    val initialMinValue = initialProductList
        .minOfOrNull { it?.skn?.toFloat() ?: 0.0f }
        ?: 0.0f
    val initialMaxValue = initialProductList
        .maxOfOrNull { it?.skn?.toFloat() ?: 0.0f }
        ?: 0.0f
    var slidingRange by remember {
        mutableStateOf(initialMinValue..initialMaxValue)
    }
    when (option) {
        ProductFilterOption.SKN -> {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortOptionItem(
    option: ProductSortOption,
    onSortSelected: (ProductSortOption) -> Unit,
    currentSortOption: ProductSortOption
) {
    var selectedOption by remember { mutableStateOf(false) }
    selectedOption = currentSortOption == option

    FilterChip(
        label = {
            Text(
                text = option.sortOption,
                style = MaterialTheme.typography.labelMedium
            )
        },
        onClick = { onSortSelected(option) },
        selected = selectedOption,
        leadingIcon = if (selectedOption) {
            {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_check_24),
                    contentDescription = Constants.EMPTY_STRING,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}

