package id.dev.spendless.transaction.presentation

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.add_transaction.AddTransactionScreenRoot
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.SpendLessFab
import id.dev.spendless.core.presentation.design_system.component.transaction.EmptyTransaction
import id.dev.spendless.core.presentation.design_system.component.transaction.TransactionLazyList
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.transaction.presentation.component.AllTransactionTopAppBar
import kotlinx.coroutines.launch
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
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllTransactionScreen(
    state: AllTransactionState,
    onAction: (AllTransactionAction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AllTransactionTopAppBar(
                modifier = Modifier,
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
                    scope.launch {
                        showBottomSheet = true
                    }
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
            if (state.allTransactions.isNotEmpty()) {
                TransactionLazyList(
                    allTransactions = state.allTransactions,
                    onItemClick = {
                        onAction(AllTransactionAction.OnItemTransactionClick(it))
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(top = 8.dp, start = 16.dp, end = 8.dp),
                )
            } else {
                EmptyTransaction(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            AnimatedVisibility(
                visible = showBottomSheet
            ) {
                AddTransactionScreenRoot(
                    sheetState = sheetState,
                    onDismissRequest = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    }
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