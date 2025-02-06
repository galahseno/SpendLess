package id.dev.spendless.transaction.presentation.all_transaction.export

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.transaction.presentation.all_transaction.AllTransactionAction
import id.dev.spendless.transaction.presentation.all_transaction.AllTransactionState
import id.dev.spendless.transaction.presentation.all_transaction.AllTransactionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExportScreenRoot(

    viewModel: AllTransactionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    ExportScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ExportScreen(
    state: AllTransactionState,
    onAction: (AllTransactionAction) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
private fun ExportScreenPreview() {
    SpendLessTheme {
        ExportScreen(
            state = AllTransactionState(),
            onAction = {}
        )
    }
}