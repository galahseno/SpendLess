package id.dev.spendless.core.presentation.design_system.component.preferences

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum

@Composable
fun ExpensesFormat(
    selectedExpenseFormat: ExpensesFormatEnum,
    currency: String,
    modifier: Modifier = Modifier,
    onExpensesSelected: (ExpensesFormatEnum) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(buttonBackground.copy(alpha = .08f))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ExpensesFormatEnum.entries.forEach { expensesFormat ->
            AnimatedContent(
                label = "animated_expense_format",
                modifier = Modifier.weight(1f),
                targetState = selectedExpenseFormat,
                transitionSpec = {
                    getExpensesFormatTransitionSpec(expensesFormat, targetState)
                }
            ) {
                BackgroundBox(
                    text = if (expensesFormat == ExpensesFormatEnum.MinusPrefix) "-${currency}10"
                    else "(${currency}10)",
                    modifier = Modifier.weight(1f),
                    isSelected = it == expensesFormat,
                    onBoxSelected = {
                        onExpensesSelected(expensesFormat)
                    }
                )
            }

        }
    }
}

private fun AnimatedContentTransitionScope<ExpensesFormatEnum>.getExpensesFormatTransitionSpec(
    expensesFormat: ExpensesFormatEnum,
    targetState: ExpensesFormatEnum
) = when {
    /**
     * If the expensesFormat is MinusPrefix and target is RoundBrackets
     **/
    expensesFormat == ExpensesFormatEnum.MinusPrefix && targetState == ExpensesFormatEnum.RoundBrackets -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        )
    }
    /**
     * If the expensesFormat is MinusPrefix
     **/
    expensesFormat == ExpensesFormatEnum.MinusPrefix -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        ) togetherWith ExitTransition.None
    }
    /**
     * If the target is MinusPrefix
     **/
    targetState == ExpensesFormatEnum.MinusPrefix -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        )
    }
    /**
     * Default Case
     **/
    else -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        ) togetherWith ExitTransition.None
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpensesFormatPreview() {
    SpendLessTheme {
        Box(
            Modifier
                .height(65.dp)
                .width(500.dp)
                .background(screenBackground),
            contentAlignment = Alignment.Center
        ) {
            ExpensesFormat(
                selectedExpenseFormat = ExpensesFormatEnum.RoundBrackets,
                currency = CurrencyEnum.USD.symbol,
                onExpensesSelected = {

                }
            )
        }
    }
}