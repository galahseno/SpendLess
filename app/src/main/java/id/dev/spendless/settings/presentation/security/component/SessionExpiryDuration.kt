package id.dev.spendless.settings.presentation.security.component

import androidx.compose.animation.AnimatedContent
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
import id.dev.spendless.core.presentation.design_system.component.preferences.BackgroundBox
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.settings.presentation.model.SessionExpiredEnum

@Composable
fun SessionExpiryDuration(
    selectedDuration: SessionExpiredEnum,
    onSelectedDuration: (SessionExpiredEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    var fromSeparator by remember {
        mutableStateOf(selectedDuration)
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
        SessionExpiredEnum.entries.forEach { duration ->
            AnimatedContent(
                label = "animated_expense_format",
                modifier = Modifier.weight(1f),
                targetState = selectedDuration,
                // TODO handle animation
//                transitionSpec = {
//                    getThousandSeparatorTransitionSpec(separator, fromSeparator, targetState)
//                }
            ) {
                if (this.transition.currentState == this.transition.targetState) {
                    fromSeparator = selectedDuration
                }
                BackgroundBox(
                    text = duration.toString(),
                    modifier = Modifier.weight(1f),
                    isSelected = it == duration,
                    onBoxSelected = {
                        onSelectedDuration(duration)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionExpiryDurationPreview() {
    SpendLessTheme {
        Box(
            Modifier
                .height(65.dp)
                .width(500.dp)
                .background(screenBackground),
            contentAlignment = Alignment.Center
        ) {
            SessionExpiryDuration(
                selectedDuration = SessionExpiredEnum.THIRTY_MINUTES,
                onSelectedDuration = {

                }
            )
        }
    }
}