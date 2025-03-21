package id.dev.spendless.core.presentation.design_system.component.preferences

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.design_system.unselectedColor
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum

@Composable
fun ExpensesFormat(
    selectedExpenseFormat: ExpensesFormatEnum,
    currency: String,
    modifier: Modifier = Modifier,
    onExpensesSelected: (ExpensesFormatEnum) -> Unit
) {
    val backgroundColor = buttonBackground.copy(alpha = .08f)
    val selectedColor = componentBackground

    var rowWidthPx by remember { mutableFloatStateOf(1f) }
    val density = LocalDensity.current
    val rowWidthDp = with(density) { rowWidthPx.toDp() }

    val selectedWidthDp = rowWidthDp / ExpensesFormatEnum.entries.size
    val offsetX = remember { Animatable(0f) }

    LaunchedEffect(selectedExpenseFormat, rowWidthPx) {
        val selectedWidthPx = rowWidthPx / ExpensesFormatEnum.entries.size
        val targetX = ExpensesFormatEnum.valueOf(selectedExpenseFormat.toString()).ordinal * selectedWidthPx
        offsetX.animateTo(
            targetValue = targetX,
            animationSpec = tween(300)
        )
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
            .padding(4.dp)
            .onSizeChanged { newSize ->
                rowWidthPx = newSize.width.toFloat()
            }
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.toInt(), 0) }
                .width(selectedWidthDp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(selectedColor)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ExpensesFormatEnum.entries.forEach { expensesFormat ->
                val isSelected = expensesFormat == selectedExpenseFormat

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .pointerInput(Unit) { detectTapGestures { onExpensesSelected(expensesFormat) } },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (expensesFormat == ExpensesFormatEnum.MinusPrefix) "-${currency}10"
                        else "(${currency}10)",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 16.sp,
                            color = if (isSelected) Color.Black else unselectedColor
                        )
                    )
                }
            }
        }
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
                currency = CurrencyEnum.USD.symbol,
                onExpensesSelected = {

                }
            )
        }
    }
}