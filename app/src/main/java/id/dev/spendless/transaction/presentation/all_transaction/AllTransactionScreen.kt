package id.dev.spendless.transaction.presentation.all_transaction

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.SpendLessFab
import id.dev.spendless.core.presentation.design_system.component.transaction.EmptyTransaction
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.transaction.presentation.component.TopAppBarAllTransaction
import org.koin.androidx.compose.koinViewModel

@Composable
fun AllTransactionScreenRoot(
    onBackClick: () -> Unit,
    viewModel: AllTransactionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
    }

    ObserveAsEvents(viewModel.event) {

    }

    AllTransactionScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is AllTransactionAction.OnBackClick -> onBackClick()
                else -> {}
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun AllTransactionScreen(
    state: AllTransactionState,
    onAction: (AllTransactionAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarAllTransaction(
                onBackClick = {
                    onAction(AllTransactionAction.OnBackClick)
                },
                onExportClick = {
                    // TODO Export sheet
                },
            )
        },
        containerColor = screenBackground,
        floatingActionButton = {
            SpendLessFab(
                onClick = {
                    onAction(AllTransactionAction.OnAddTransactionClick)
                }
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(it),
            contentAlignment = Alignment.Center
        ) {
            if (state.transaction) {

            } else {
                EmptyTransaction(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
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