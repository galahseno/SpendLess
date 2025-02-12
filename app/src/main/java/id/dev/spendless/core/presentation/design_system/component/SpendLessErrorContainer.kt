package id.dev.spendless.core.presentation.design_system.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.errorBackground

@Composable
fun SpendLessErrorContainer(
    isErrorVisible: Boolean,
    errorHeightDp: Dp,
    errorMessage: String,
    keyboardOpen: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isErrorVisible,
        label = "anime_error_container",
        modifier = modifier,
        enter = slideIn(initialOffset = { IntOffset(0, it.height / 2) }) + fadeIn(),
        exit = slideOut(targetOffset = { IntOffset(0, it.height / 2) }) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(errorHeightDp)
                .background(errorBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = errorMessage,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W500),
                modifier = Modifier
                    .then(
                        if (keyboardOpen) Modifier
                        else Modifier.navigationBarsPadding()
                    )
            )
        }
    }
}

@Preview
@Composable
private fun SpendLessErrorContainerPreview() {
    SpendLessTheme {
        SpendLessErrorContainer(
            isErrorVisible = true,
            errorHeightDp = 120.dp,
            errorMessage = "Something Wrong",
            keyboardOpen = false
        )
    }
}
