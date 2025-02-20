package id.dev.spendless.transaction.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.screenBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionTopAppBar(
    onBackClick: () -> Unit,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "back"
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.all_transactions),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 20.sp
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        actions = {
            IconButton(
                onClick = onExportClick,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.download),
                    contentDescription = "export",
                    tint = Color.Black,
                )
            }
        }
    )
}

@Preview
@Composable
private fun TopAppBarAllTransactionPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier.background(screenBackground)
        ) {
            AllTransactionTopAppBar(
                onBackClick = {},
                onExportClick = {}
            )
        }
    }
}