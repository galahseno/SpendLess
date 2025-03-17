package id.dev.spendless.core.presentation.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun keyboardOpenAsState(): State<Boolean> {
    val bottomWindow = WindowInsets.ime.getBottom(LocalDensity.current)
    return rememberUpdatedState(bottomWindow > 0)
}