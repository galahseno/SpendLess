package id.dev.spendless.transaction.presentation.add_transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddTransactionScreenRoot(

    viewModel: AddTransactionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    AddTransactionScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun AddTransactionScreen(
    state: AddTransactionState,
    onAction: (AddTransactionAction) -> Unit
) {

}

@Preview(showBackground = true)
@Composable
private fun AddTransactionScreenPreview() {
    SpendLessTheme {
        AddTransactionScreen(
            state = AddTransactionState(),
            onAction = {}
        )
    }
}