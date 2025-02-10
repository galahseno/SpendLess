package id.dev.spendless.core.presentation.design_system.component.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme

@Composable
fun EmptyTransaction(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "\uD83D\uDCB8",
            fontSize = 114.sp
        )
        Text(
            text = stringResource(R.string.no_transactions),
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyTransactionPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .padding(20.dp)
        ) {
            EmptyTransaction(
                modifier = Modifier
            )
        }

    }
}