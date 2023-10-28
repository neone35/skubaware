package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product

@Composable
fun BottomCounterText(
    modifier: Modifier,
    productList: List<Product?>
) {
    val listSize = productList.size
    val listQuantitySum = countSumOfQuantities(productList)

    val listSizeTitle = stringResource(R.string.in_the_list) + " " + listSize
    val listQuantitySumTitle =
        listQuantitySum.toString() + " " + stringResource(R.string.of_quantity)

    Text(
        modifier = modifier.then(
            Modifier
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ),
        style = MaterialTheme.typography.labelMedium,
        text = "$listSizeTitle / $listQuantitySumTitle"
    )
}

fun countSumOfQuantities(
    productList: List<Product?>
): Double {
    var sumOfQuantities = 0.0
    productList.forEach {
        it?.quantity?.toDouble()?.let { quantity ->
            sumOfQuantities += quantity
        }
    }
    return sumOfQuantities
}