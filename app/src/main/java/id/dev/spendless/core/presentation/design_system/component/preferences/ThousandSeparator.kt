package id.dev.spendless.core.presentation.design_system.component.preferences

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum

@Composable
fun ThousandSeparator(
    selectedSeparator: ThousandsSeparatorEnum,
    modifier: Modifier = Modifier,
    onExpensesSelected: (ThousandsSeparatorEnum) -> Unit
) {
    var previousSeparator by remember { mutableStateOf(selectedSeparator) }

    LaunchedEffect(selectedSeparator) {
        previousSeparator = selectedSeparator
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(buttonBackground.copy(alpha = .2f))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThousandsSeparatorEnum.entries.forEach { separator ->
            AnimatedContent(
                label = "animated_expense_format",
                modifier = Modifier.weight(1f),
                targetState = selectedSeparator,
                transitionSpec = {
                    when (targetState) {
                        ThousandsSeparatorEnum.Dot -> {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right
                            ) togetherWith slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right
                            )
                        }

                        ThousandsSeparatorEnum.Space -> {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left
                            ) togetherWith slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left
                            )
                        }

                        else -> {
                            // TODO Fix animation
                            if (separator == ThousandsSeparatorEnum.Space) slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left
                            ) togetherWith slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left
                            )
                            else if (separator == ThousandsSeparatorEnum.Dot) slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right
                            ) togetherWith slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right
                            )
                            else slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right
                            ) togetherWith fadeOut()
                        }
                    }
                }
            ) {
                SeparatorBox(
                    text = when (separator) {
                        ThousandsSeparatorEnum.Dot -> "1.000"
                        ThousandsSeparatorEnum.Comma -> "1,000"
                        else -> "1 000"
                    },
                    modifier = Modifier.weight(1f),
                    isSelected = it == separator,
                    onSeparatorSelected = {
                        onExpensesSelected(separator)
                    }
                )
            }

        }
    }
}

@Composable
private fun SeparatorBox(
    isSelected: Boolean,
    text: String,
    onSeparatorSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO Animated selected background only
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
                onSeparatorSelected()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 16.sp)
        )
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