package id.dev.spendless.settings.presentation.preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.settings.presentation.SettingsAction
import id.dev.spendless.settings.presentation.SettingsState
import id.dev.spendless.settings.presentation.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreferencesScreenRoot(

    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    PreferencesScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun PreferencesScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
private fun PreferencesScreenPreview() {
    SpendLessTheme {
        PreferencesScreen(
            state = SettingsState(),
            onAction = {}
        )
    }
}