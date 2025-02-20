package id.dev.spendless.dashboard.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.domain.model.CategoryWithEmoji
import id.dev.spendless.core.domain.model.LargestTransaction
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.balanceDashboardColor
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.gradientBackground
import id.dev.spendless.core.presentation.design_system.keyPadBackground
import id.dev.spendless.core.presentation.design_system.secondaryColor
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import id.dev.spendless.dashboard.presentation.util.formatTimestamp

@Composable
fun DashboardSummary(
    balance: String,
    negativeBalance: Boolean,
    previousWeekSpend: String,
    largestTransactionAllTime: CategoryWithEmoji?,
    largestTransaction: LargestTransaction?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier
                .weight(0.9f),
            verticalArrangement = Arrangement.spacedBy(
                2.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TODO check spacing when no largest transactionCategory
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = balance,
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = if (negativeBalance) Color.White
                    else balanceDashboardColor
                ),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.account_balance),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontSize = 14.sp
                )
            )
        }
        if (largestTransactionAllTime?.categoryName != null && largestTransactionAllTime.categoryEmoji != null) {
            LargestTransactionCategoryAllTime(
                categoryName = largestTransactionAllTime.categoryName,
                categoryEmoji = largestTransactionAllTime.categoryEmoji,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(componentBackground.copy(0.2f))
                    .padding(8.dp)
            )
        }
        Row(
            modifier = Modifier
                .weight(0.35f)
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .height(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(keyPadBackground),
                contentAlignment = Alignment.Center
            ) {
                if (largestTransaction?.createdAt == null && largestTransaction?.transactionName == null
                    && largestTransaction?.amount == null
                ) {
                    EmptyLargestTransaction()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(0.4f),
                                text = largestTransaction.transactionName ?: "",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 20.sp
                                ),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(1f),
                                text = largestTransaction.amount ?: "",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.End
                                ),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.largest_transaction),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 12.sp,
                                )
                            )
                            Text(
                                text = largestTransaction.createdAt ?: "",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 12.sp,
                                )
                            )
                        }
                    }
                }
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
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis
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

@Composable
private fun LargestTransactionCategoryAllTime(
    categoryName: String,
    categoryEmoji: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = categoryEmoji,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 30.sp),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(keyPadBackground)
                .padding(8.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 20.sp,
                    color = Color.White
                )
            )
            Text(
                text = stringResource(R.string.most_popular_category),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 12.sp,
                    color = componentBackground.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
fun EmptyLargestTransaction(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(R.string.empty_large_transaction),
        style = MaterialTheme.typography.headlineMedium.copy(
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        ),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun DashboardStateSummaryPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
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
                negativeBalance = false,
                previousWeekSpend = "0"
                    .formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.MinusPrefix,
                        currency = CurrencyEnum.USD,
                        decimal = DecimalSeparatorEnum.Dot,
                        thousands = ThousandsSeparatorEnum.Comma
                    ),
                largestTransactionAllTime = CategoryWithEmoji(
                    categoryName = "Food & Groceries",
                    categoryEmoji = "\uD83C\uDF55"
                ),
                largestTransaction = LargestTransaction(
                    transactionName = "Adobe Yearly",
                    amount = "-59.99".formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.MinusPrefix,
                        currency = CurrencyEnum.USD,
                        decimal = DecimalSeparatorEnum.Dot,
                        thousands = ThousandsSeparatorEnum.Comma
                    ),
                    createdAt = System.currentTimeMillis().formatTimestamp(),
                ),
//                largestTransactionAllTime = null,
//                largestTransaction = null
            )
        }
    }
}