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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum

@Composable
fun ThousandSeparator(
    selectedSeparator: ThousandsSeparatorEnum,
    onExpensesSelected: (ThousandsSeparatorEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    var fromSeparator by remember {
        mutableStateOf(selectedSeparator)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(buttonBackground.copy(alpha = .08f))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThousandsSeparatorEnum.entries.forEach { separator ->
            AnimatedContent(
                label = "animated_expense_format",
                modifier = Modifier.weight(1f),
                targetState = selectedSeparator,
                transitionSpec = {
                    getThousandSeparatorTransitionSpec(separator, fromSeparator, targetState)
                }
            ) {
                if (this.transition.currentState == this.transition.targetState) {
                    fromSeparator = selectedSeparator
                }
                BackgroundBox(
                    text = when (separator) {
                        ThousandsSeparatorEnum.Dot -> "1.000"
                        ThousandsSeparatorEnum.Comma -> "1,000"
                        else -> "1 000"
                    },
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

private fun AnimatedContentTransitionScope<ThousandsSeparatorEnum>.getThousandSeparatorTransitionSpec(
    separator: ThousandsSeparatorEnum,
    fromSeparator: ThousandsSeparatorEnum,
    targetState: ThousandsSeparatorEnum
) = when {
    /**
     * If the separator is Dot and target is Comma and fromSeparator is Dot
     **/
    separator == ThousandsSeparatorEnum.Dot && targetState == ThousandsSeparatorEnum.Comma
            && fromSeparator == ThousandsSeparatorEnum.Dot -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        )
    }
    /**
     * If the separator is Dot and target is Dot and fromSeparator is Comma
     **/
    separator == ThousandsSeparatorEnum.Dot && targetState == ThousandsSeparatorEnum.Dot
            && fromSeparator == ThousandsSeparatorEnum.Comma -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        ) togetherWith ExitTransition.None
    }
    /**
     * If the separator is Dot and target is Dot and fromSeparator is Space
     **/
    separator == ThousandsSeparatorEnum.Dot && targetState == ThousandsSeparatorEnum.Dot
            && fromSeparator == ThousandsSeparatorEnum.Space -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        ) togetherWith ExitTransition.None
    }
    /**
     * If the separator is Dot and target is Space and fromSeparator is Dot
     **/
    separator == ThousandsSeparatorEnum.Dot && targetState == ThousandsSeparatorEnum.Space
            && fromSeparator == ThousandsSeparatorEnum.Dot -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        )
    }
    /**
     * If the separator is Comma and target is Space and fromSeparator is Comma
     **/
    separator == ThousandsSeparatorEnum.Comma && targetState == ThousandsSeparatorEnum.Space
            && fromSeparator == ThousandsSeparatorEnum.Comma -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        )
    }
    /**
     * If the separator is Comma and target is Comma and fromSeparator is Dot
     **/
    separator == ThousandsSeparatorEnum.Comma && targetState == ThousandsSeparatorEnum.Comma
            && fromSeparator == ThousandsSeparatorEnum.Dot -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        ) togetherWith ExitTransition.None
    }
    /**
     * If the separator is Comma and target is Comma and fromSeparator is Space
     **/
    separator == ThousandsSeparatorEnum.Comma && targetState == ThousandsSeparatorEnum.Comma
            && fromSeparator == ThousandsSeparatorEnum.Space -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        ) togetherWith ExitTransition.None
    }
    /**
     * If the separator is Comma and target is Dot and fromSeparator is Comma
     **/
    separator == ThousandsSeparatorEnum.Comma && targetState == ThousandsSeparatorEnum.Dot
            && fromSeparator == ThousandsSeparatorEnum.Comma -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        )
    }
    /**
     * If the separator is Space and target is Dot and fromSeparator is Space
     **/
    separator == ThousandsSeparatorEnum.Space && targetState == ThousandsSeparatorEnum.Dot
            && fromSeparator == ThousandsSeparatorEnum.Space -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        )
    }
    /**
     * If the separator is Space and target is Space and fromSeparator is Comma
     **/
    separator == ThousandsSeparatorEnum.Space && targetState == ThousandsSeparatorEnum.Space
            && fromSeparator == ThousandsSeparatorEnum.Comma -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        ) togetherWith ExitTransition.None
    }
    /**
     * If the separator is Space and target is Space and fromSeparator is Dot
     **/
    separator == ThousandsSeparatorEnum.Space && targetState == ThousandsSeparatorEnum.Space
            && fromSeparator == ThousandsSeparatorEnum.Dot -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        )  togetherWith ExitTransition.None
    }
    /**
     * If the separator is Space and target is Comma and fromSeparator is Space
     **/
    separator == ThousandsSeparatorEnum.Space && targetState == ThousandsSeparatorEnum.Comma
            && fromSeparator == ThousandsSeparatorEnum.Space -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        )
    }


    // Default case
    else -> {
        EnterTransition.None togetherWith ExitTransition.None
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
            ThousandSeparator(
                selectedSeparator = ThousandsSeparatorEnum.Dot,
                onExpensesSelected = {

                }
            )
        }
    }
}