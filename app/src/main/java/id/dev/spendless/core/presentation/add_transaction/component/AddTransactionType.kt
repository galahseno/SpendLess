package id.dev.spendless.core.presentation.add_transaction.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material3.Icon
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
import id.dev.spendless.core.presentation.design_system.unselectedColor
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum

@Composable
fun AddTransactionType(
    selectedTransactionType: TransactionTypeEnum,
    onTransactionTypeSelected: (TransactionTypeEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(buttonBackground.copy(alpha = .08f))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TransactionTypeEnum.entries.forEach { type ->
            AnimatedContent(
                label = "animated_expense_format",
                modifier = Modifier.weight(1f),
                targetState = selectedTransactionType,
                transitionSpec = {
                    getTransactionTypeSeparatorTransitionSpec(type, targetState)
                }
            ) {
                TransactionBox(
                    text = type.name,
                    modifier = Modifier.weight(1f),
                    isSelected = it == type,
                    onTypeSelected = {
                        onTransactionTypeSelected(type)
                    }
                )
            }

        }
    }
}

@Composable
private fun TransactionBox(
    isSelected: Boolean,
    text: String,
    onTypeSelected: () -> Unit,
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
                onTypeSelected()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = if (text == TransactionTypeEnum.Expenses.name)
                    Icons.AutoMirrored.Rounded.TrendingDown
                else Icons.AutoMirrored.Rounded.TrendingUp,
                contentDescription = "",
                tint = if (isSelected) buttonBackground else unselectedColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 16.sp,
                    color = if (isSelected) buttonBackground else unselectedColor
                )
            )
        }
    }
}

private fun AnimatedContentTransitionScope<TransactionTypeEnum>.getTransactionTypeSeparatorTransitionSpec(
    separator: TransactionTypeEnum,
    targetState: TransactionTypeEnum
) = when {
    /**
     * If the separator is Expense and target is Income
     **/
    separator == TransactionTypeEnum.Expenses && targetState == TransactionTypeEnum.Income -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        )
    }

    /**
     * If the separator is Expenses
     **/
    separator == TransactionTypeEnum.Expenses -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        ) togetherWith ExitTransition.None
    }
    /**
     * If the target is Expense
     **/
    targetState == TransactionTypeEnum.Expenses -> {
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

@Preview
@Composable
private fun AddTransactionTypePreview() {
    SpendLessTheme {
        Box(
            Modifier
                .height(65.dp)
                .width(500.dp)
                .background(screenBackground),
            contentAlignment = Alignment.Center
        ) {
            AddTransactionType(
                selectedTransactionType = TransactionTypeEnum.Expenses,
                onTransactionTypeSelected = {

                }
            )
        }
    }
}