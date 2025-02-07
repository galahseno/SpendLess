package id.dev.spendless.core.presentation.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.registerTextFieldBackground

@Composable
fun PinDotView(
    pin: String,
    modifier: Modifier = Modifier,
    pinLength: Int = 5,
    emptyDotColor: Color = registerTextFieldBackground,
    filledDotColor: Color = buttonBackground
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp, alignment = CenterHorizontally)
    ) {
        repeat(pinLength) { index ->
            val dotColor = if (index < pin.length) filledDotColor else emptyDotColor
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PinDotViewPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier.size(300.dp),
            contentAlignment = Alignment.Center
        ) {
            PinDotView(
                pin = "12"
            )
        }
    }
}