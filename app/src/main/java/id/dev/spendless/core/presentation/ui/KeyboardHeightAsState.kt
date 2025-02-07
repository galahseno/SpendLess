package id.dev.spendless.core.presentation.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun keyboardHeightAsState(): State<Dp> {
    val bottomWindow = WindowInsets.ime.getBottom(LocalDensity.current)
    var keyboardHeightAsDp: Dp
    with(LocalDensity.current) {
        keyboardHeightAsDp = bottomWindow.toDp()
    }

    return rememberUpdatedState(keyboardHeightAsDp)
}