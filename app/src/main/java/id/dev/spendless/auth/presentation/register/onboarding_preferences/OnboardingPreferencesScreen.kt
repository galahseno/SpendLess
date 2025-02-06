package id.dev.spendless.auth.presentation.register.onboarding_preferences

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.auth.presentation.register.RegisterAction
import id.dev.spendless.auth.presentation.register.RegisterState
import id.dev.spendless.auth.presentation.register.RegisterViewModel
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingPreferencesScreenRoot(

    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    OnboardingPreferencesScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun OnboardingPreferencesScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
private fun OnboardingPreferencesScreenPreview() {
    SpendLessTheme {
        OnboardingPreferencesScreen(
            state = RegisterState(),
            onAction = {}
        )
    }
}