package id.dev.spendless.dashboard.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.gradientBackground
import id.dev.spendless.core.presentation.design_system.keyPadBackground
import id.dev.spendless.core.presentation.design_system.secondaryColor
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum

@Composable
fun DashboardSummary(
    balance: String,
    previousWeekSpend: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = balance,
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.White)
            )
            Text(
                text = stringResource(R.string.account_balance),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontSize = 14.sp
                )
            )
        }
        Row(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.6f)
                    .height(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(keyPadBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.empty_large_transaction),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .height(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(secondaryColor)
                    .padding(12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = previousWeekSpend,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                    Text(
                        text = stringResource(R.string.previous_week),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DashboardStateSummaryPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .background(gradientBackground)
                .padding(10.dp),
        ) {
            DashboardSummary(
                balance = "-10382.45"
                    .formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.MinusPrefix,
                        currency = CurrencyEnum.USD,
                        decimal = DecimalSeparatorEnum.Dot,
                        thousands = ThousandsSeparatorEnum.Comma
                    ),
                previousWeekSpend = "0"
                    .formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.MinusPrefix,
                        currency = CurrencyEnum.USD,
                        decimal = DecimalSeparatorEnum.Dot,
                        thousands = ThousandsSeparatorEnum.Comma
                    )
            )
        }
    }
}