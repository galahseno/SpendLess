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
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum

@Composable
fun DecimalSeparator(
    selectedSeparator: DecimalSeparatorEnum,
    onExpensesSelected: (DecimalSeparatorEnum) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(buttonBackground.copy(alpha = .08f))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DecimalSeparatorEnum.entries.forEach { separator ->
            AnimatedContent(
                label = "animated_expense_format",
                modifier = Modifier.weight(1f),
                targetState = selectedSeparator,
                transitionSpec = {
                    getDecimalSeparatorTransitionSpec(separator, targetState)
                }
            ) {
                BackgroundBox(
                    text = if (separator == DecimalSeparatorEnum.Dot) "1.00"
                    else "1,00",
                    modifier = Modifier.weight(1f),
                    isSelected = it == separator,
                    onBoxSelected = {
                        onExpensesSelected(separator)
                    }
                )
            }

        }
    }
}

private fun AnimatedContentTransitionScope<DecimalSeparatorEnum>.getDecimalSeparatorTransitionSpec(
    separator: DecimalSeparatorEnum,
    targetState: DecimalSeparatorEnum
) = when {
    /**
     * If the separator is Dot and target is Comma
     **/
    separator == DecimalSeparatorEnum.Dot && targetState == DecimalSeparatorEnum.Comma -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        )
    }

    /**
     * If the separator is Dot
     **/
    separator == DecimalSeparatorEnum.Dot -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        ) togetherWith ExitTransition.None
    }
    /**
     * If the target is Dot
     **/
    targetState == DecimalSeparatorEnum.Dot -> {
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
            DecimalSeparator(
                selectedSeparator = DecimalSeparatorEnum.Dot,
                onExpensesSelected = {

                }
            )
        }
    }
}