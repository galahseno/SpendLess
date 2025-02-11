package id.dev.spendless.core.presentation.design_system.component.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.screenBackground

@Composable
fun TotalSpendCard(
    totalSpend: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = componentBackground,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = totalSpend,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 32.sp)
            )
            Text(
                text = stringResource(R.string.spend_this_month),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TotalSpendCardPreview() {
    SpendLessTheme {
        Box(
            Modifier
                .height(150.dp)
                .width(500.dp)
                .background(screenBackground)
        ) {
            TotalSpendCard(
                totalSpend = "$10000",
                Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(10.dp)
            )
        }
    }
}