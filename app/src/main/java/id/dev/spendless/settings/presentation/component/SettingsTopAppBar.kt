package id.dev.spendless.settings.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.screenBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopAppBar(
    titleAppBar: String,
    onBackClick: () -> Unit,
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
                text = titleAppBar,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 20.sp
                )
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsTopAppBarPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier.background(screenBackground)
        ) {
            SettingsTopAppBar(
                onBackClick = {},
                titleAppBar = stringResource(R.string.settings)
            )
        }
    }
}