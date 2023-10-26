package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme
import com.arturmaslov.tgnba.utils.Constants

@Preview(showBackground = true)
@Composable
fun ProductListPreview() {
    SkubaWareTheme {
        ProductList(
            listOf(
                Product(
                    imgUrl = null,
                    quantity = 2,
                    skn = "FSDFSDF",
                    brand = "Skulabamba",
                    name = "padangeles",
                    buyerCode = 45415
                ),
                Product(
                    imgUrl = null,
                    quantity = 2,
                    skn = "FSDFSDF",
                    brand = "Skulabamba",
                    name = "padangeles",
                    buyerCode = 45415
                )
            )
        )
    }
}

@Composable
fun ProductList(productList: List<Product?>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        itemsIndexed(productList) { index, product ->
            ProductCard(
                name = product?.name,
                imgUrl = product?.imgUrl
            )
        }
    }
}

@Composable
fun ProductCard(name: String?, imgUrl: String?) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Fit,
            )
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = name ?: Constants.EMPTY_STRING,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = name ?: Constants.EMPTY_STRING,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}