package id.dev.spendless.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingScreenRoot(

    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    SettingScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun SettingScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SpendLessTheme {
        SettingScreen(
            state = SettingsState(),
            onAction = {}
        )
    }
}