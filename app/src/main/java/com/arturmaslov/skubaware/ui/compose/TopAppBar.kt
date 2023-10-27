package com.arturmaslov.skubaware.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arturmaslov.skubaware.R
import com.arturmaslov.skubaware.ui.theme.SkubaWareTheme
import com.arturmaslov.tgnba.utils.Constants

@Preview(showBackground = true)
@Composable
fun SkubaTopAppBarPreview() {
    SkubaWareTheme {
        SkubaTopAppBar()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkubaTopAppBar() {
    TopAppBar(
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = Constants.EMPTY_STRING,
                contentScale = ContentScale.None,
                modifier = Modifier.size(40.dp)
            )
        },
        title = {
            Text(
                stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.Top
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_filter_alt_24),
                        contentDescription = Constants.EMPTY_STRING,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                val uriHandler = LocalUriHandler.current
                IconButton(onClick = { uriHandler.openUri(Constants.AUTHOR_URL) }) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_globe_24),
                        contentDescription = Constants.EMPTY_STRING,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    )
}