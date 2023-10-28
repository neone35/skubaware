package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.data.models.Product
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme
import com.arturmaslov.tgnba.utils.Constants
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin

@Preview(showBackground = true)
@Composable
fun ProductListPreview() {
    SkubaWareTheme {
        ProductList(
            listOf(
                Product(
                    imgUrl = null,
                    quantity = "2",
                    skn = "FSDFSDF",
                    brand = "Skulabamba",
                    name = "padangeles",
                    buyerCode = "484849"
                ),
                Product(
                    imgUrl = null,
                    quantity = "2",
                    skn = "FSDFSDF",
                    brand = "Skulabamba",
                    name = "padangeles",
                    buyerCode = "484849"
                )
            ),
            onClick = {},
            endIconId = R.drawable.ic_add_24
        )

    }
}

@Composable
fun ProductList(
    productList: List<Product?>,
    modifier: Modifier = Modifier,
    onClick: (Product) -> Unit,
    endIconId: Int
) {
    Box(
        modifier.then(Modifier.fillMaxHeight())
    ) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            itemsIndexed(productList) { index, product ->
                product?.let {
                    ProductCard(
                        product = it,
                        onClick,
                        endIconId
                    )
                }
            }
        }
        BottomCounterText(
            productList = productList,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    onClick: (Product) -> Unit,
    endIconId: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            GlideImage(
                imageModel = { product.imgUrl },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                ),
                component = rememberImageComponent {
                    +PlaceholderPlugin.Loading(painterResource(id = R.drawable.ic_logo))
                    +PlaceholderPlugin.Failure(painterResource(id = R.drawable.ic_logo))
                },
                previewPlaceholder = R.drawable.ic_logo,
                modifier = Modifier
                    .size(100.dp)
            )
            MainDataColumn(
                modifier = Modifier
                    .weight(1.0f)
                    .padding(top = 12.dp, end = 12.dp, bottom = 12.dp),
                product = product
            )
            AmountWithIconRow(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable { onClick(product) },
                product = product,
                iconId = endIconId
            )
        }
    }
}

@Composable
fun MainDataColumn(
    modifier: Modifier,
    product: Product?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        IdentifierRow(product)
        Text(
            text = product?.brand ?: Constants.EMPTY_STRING,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = product?.name ?: Constants.EMPTY_STRING,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
fun IdentifierRow(
    product: Product?
) {
    val skn =
        if (product?.skn?.isNotEmpty() == true)
            ("SKN#" + product.skn) else Constants.EMPTY_STRING
    val buyerCode =
        if (product?.buyerCode?.isNotEmpty() == true)
            ("BC#" + product.buyerCode) else Constants.EMPTY_STRING
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = skn,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = buyerCode,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun AmountWithIconRow(
    modifier: Modifier,
    product: Product?,
    iconId: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = product?.quantity ?: Constants.EMPTY_STRING,
            style = MaterialTheme.typography.labelLarge,
        )
        Image(
            painter = painterResource(id = iconId),
            contentDescription = Constants.EMPTY_STRING,
            contentScale = ContentScale.None,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            modifier = Modifier.size(40.dp)
        )
    }
}
