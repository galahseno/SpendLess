package id.dev.spendless.core.presentation.export.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportAppBar(
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets.waterfall,
        title = {
            Text(
                text = stringResource(R.string.export),
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "close sheet",
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        onCloseClick()
                    }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun ExportHeaderPreview() {
    SpendLessTheme {
        ExportAppBar(
            modifier = Modifier.fillMaxWidth(),
            onCloseClick = {}
        )
    }
}