package id.dev.spendless.dashboard.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground

@Composable
fun LatestTransaction(
    onShowAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.latest_transactions),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
            )
            TextButton(
                onClick = onShowAllClick
            ) {
                Text(
                    text = stringResource(R.string.show_all),
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                    color = buttonBackground
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LatestTransactionPreview() {
    SpendLessTheme {
        LatestTransaction(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            onShowAllClick = {}
        )
    }
}