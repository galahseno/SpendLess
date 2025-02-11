package id.dev.spendless.core.presentation.design_system.component.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.unselectedColor

@Composable
fun BackgroundBox(
    isSelected: Boolean,
    text: String,
    onBoxSelected: () -> Unit,
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
                onBoxSelected()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 16.sp,
                color = if (isSelected) Color.Black else unselectedColor
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BackgroundBoxPreview() {
    SpendLessTheme {
        BackgroundBox(
            modifier = Modifier.fillMaxWidth(),
            isSelected = true,
            text = "1.000",
            onBoxSelected = {

            }
        )
    }
}