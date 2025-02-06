package id.dev.spendless.dashboard.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreenRoot(

    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    DashboardScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
private fun DashboardScreenPreview() {
    SpendLessTheme  {
        DashboardScreen(
            state = DashboardState(),
            onAction = {}
        )
    }
}