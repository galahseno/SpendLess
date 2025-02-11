package id.dev.spendless.core.presentation.add_transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.add_transaction.component.AddTransactionAppBar
import id.dev.spendless.core.presentation.add_transaction.component.AddTransactionType
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.sheetBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreenRoot(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onCloseModalSheet: () -> Unit,
    viewModel: AddTransactionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(0.935f),
        containerColor = sheetBackground,
        tonalElevation = 16.dp,
        sheetState = sheetState,
        shape = RoundedCornerShape(topEnd = 28.dp, topStart = 28.dp),
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        windowInsets = WindowInsets.waterfall
    ) {
        AddTransactionScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    is AddTransactionAction.OnCloseModalSheet -> onCloseModalSheet()
                    else -> {}
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Composable
private fun AddTransactionScreen(
    state: AddTransactionState,
    onAction: (AddTransactionAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp, end = 12.dp),
    ) {
        AddTransactionAppBar(
            onCloseClick = {
                onAction(AddTransactionAction.OnCloseModalSheet)
            }
        )
        AddTransactionType(
            modifier = Modifier
                .padding(start = 16.dp, end = 4.dp),
            selectedTransactionType = state.selectedTransactionType,
            onTransactionTypeSelected = {
                onAction(AddTransactionAction.OnTransactionTypeSelected(it))
            }
        )
    }
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