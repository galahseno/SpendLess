package id.dev.spendless.settings.presentation.security.component

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.component.preferences.BackgroundBox
import id.dev.spendless.core.presentation.design_system.screenBackground

@Composable
fun BiometricPromptStatus(
    status: Boolean,
    modifier: Modifier = Modifier,
    onStatusSelected: (Boolean) -> Unit
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
        listOf(true, false).forEach { selected ->
            AnimatedContent(
                label = "animated_biometric_status",
                modifier = Modifier.weight(1f),
                targetState = status,
                transitionSpec = {
                    getStatusTransitionSpec(selected, targetState)
                }
            ) {
                BackgroundBox(
                    text = if (selected) stringResource(R.string.enable)
                    else stringResource(R.string.disable),
                    modifier = Modifier.weight(1f),
                    isSelected = it == selected,
                    onBoxSelected = {
                        onStatusSelected(selected)
                    }
                )
            }

        }
    }
}

private fun AnimatedContentTransitionScope<Boolean>.getStatusTransitionSpec(
    status: Boolean,
    targetState: Boolean
) = when {
    /**
     * If the expensesFormat is MinusPrefix and target is RoundBrackets
     **/
    status && !targetState -> {
        EnterTransition.None togetherWith slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Right
        )
    }
    /**
     * If the expensesFormat is MinusPrefix
     **/
    true -> {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Left
        ) togetherWith ExitTransition.None
    }
    /**
     * If the target is MinusPrefix
     **/
    false -> {
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
private fun BiometricPromptStatusPreview() {
    SpendLessTheme {
        Box(
            Modifier
                .height(65.dp)
                .width(500.dp)
                .background(screenBackground),
            contentAlignment = Alignment.Center
        ) {
            BiometricPromptStatus(
                status = true,
                onStatusSelected = {

                }
            )
        }
    }
}