package id.dev.spendless.core.presentation.add_transaction.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.errorBackground
import id.dev.spendless.core.presentation.design_system.incomeBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum

@Composable
fun TransactionAmountTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    transactionType: TransactionTypeEnum,
    hintFormatSeparator: DecimalSeparatorEnum,
    expensesFormat: ExpensesFormatEnum,
    currency: CurrencyEnum,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChanged,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .onFocusChanged { isFocused = it.isFocused }
                .defaultMinSize(minHeight = 44.dp, minWidth = 140.dp)
                .background(Color.Transparent),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Justify,
            ),
            cursorBrush = SolidColor(buttonBackground),
            decorationBox = { innerBox ->
                Row(
                    modifier = Modifier.width(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when {
                            expensesFormat == ExpensesFormatEnum.MinusPrefix
                                    && transactionType == TransactionTypeEnum.Expenses ->
                                "-${currency.symbol}"

                            expensesFormat == ExpensesFormatEnum.RoundBrackets
                                    && transactionType == TransactionTypeEnum.Expenses ->
                                "(${currency.symbol}"

                            else -> currency.symbol
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            textAlign = TextAlign.End,
                            color = when (transactionType) {
                                TransactionTypeEnum.Income -> incomeBackground
                                TransactionTypeEnum.Expenses -> errorBackground
                            }
                        ),
                        modifier = Modifier
                            .defaultMinSize(minWidth = 35.dp, minHeight = 44.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (text.isEmpty() && !isFocused) {
                            Text(
                                text = when (hintFormatSeparator) {
                                    DecimalSeparatorEnum.Dot -> "00.00"
                                    DecimalSeparatorEnum.Comma -> "00,00"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                textAlign = TextAlign.Center
                            )
                        }
                        innerBox()
                    }
                    if (expensesFormat == ExpensesFormatEnum.RoundBrackets &&
                        transactionType == TransactionTypeEnum.Expenses
                    ) {
                        Text(
                            text = ")",
                            style = MaterialTheme.typography.titleLarge.copy(
                                textAlign = TextAlign.Start,
                                color = errorBackground
                            ),
                            modifier = Modifier
                                .defaultMinSize(minHeight = 44.dp, minWidth = 14.dp)
                        )
                    }
                }
            }
        )
        supportingText?.let {
            Spacer(modifier = Modifier.height(2.dp))
            if (text.isNotEmpty()) {
                Text(
                    text = it,
                    color = errorBackground,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionNameTextFieldPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .width(175.dp)
                .height(50.dp)
                .background(screenBackground),
            contentAlignment = Alignment.Center
        ) {
            TransactionAmountTextField(
                text = "",
                onTextChanged = {},
                transactionType = TransactionTypeEnum.Expenses,
                hintFormatSeparator = DecimalSeparatorEnum.Dot,
                expensesFormat = ExpensesFormatEnum.MinusPrefix,
                currency = CurrencyEnum.USD,
                supportingText = "Username must be unique"
            )
        }
    }
}