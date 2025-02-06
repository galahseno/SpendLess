package id.dev.spendless.transaction.presentation.all_transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun AllTransactionScreenRoot(

    viewModel: AllTransactionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    AllTransactionScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun AllTransactionScreen(
    state: AllTransactionState,
    onAction: (AllTransactionAction) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
private fun AllTransactionScreenPreview() {
    SpendLessTheme {
        AllTransactionScreen(
            state = AllTransactionState(),
            onAction = {}
        )
    }
}