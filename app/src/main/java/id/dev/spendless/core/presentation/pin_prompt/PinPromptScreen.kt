package id.dev.spendless.core.presentation.pin_prompt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun PinPromptScreenRoot(

    viewModel: PinPromptViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    PinPromptScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun PinPromptScreen(
    state: PinPromptState,
    onAction: (PinPromptAction) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
private fun PinPromptScreenPreview() {
    SpendLessTheme {
        PinPromptScreen(
            state = PinPromptState(),
            onAction = {}
        )
    }
}