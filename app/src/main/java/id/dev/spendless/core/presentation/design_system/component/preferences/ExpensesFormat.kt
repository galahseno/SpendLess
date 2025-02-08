package id.dev.spendless.core.presentation.design_system.component.preferences

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum

@Composable
fun ExpensesFormat(
    selectedExpenseFormat: ExpensesFormatEnum,
    modifier: Modifier = Modifier,
    currency: String = "$",
    onExpensesSelected: (ExpensesFormatEnum) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(buttonBackground.copy(alpha = .2f))
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
                ExpensesBox(
                    text = if (expensesFormat == ExpensesFormatEnum.MinusPrefix) "-${currency}10"
                    else "(${currency}10)",
                    modifier = Modifier.weight(1f),
                    isSelected = it == expensesFormat,
                    onExpensesSelected = {
                        onExpensesSelected(expensesFormat)
                    }
                )
            }

        }
    }
}

@Composable
private fun ExpensesBox(
    isSelected: Boolean,
    text: String,
    onExpensesSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isSelected) {
                    Modifier.background(componentBackground)
                } else {
                    Modifier
                }
            )
            .clickable {
                onExpensesSelected()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 16.sp)
        )
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
                onExpensesSelected = {

                }
            )
        }
    }
}