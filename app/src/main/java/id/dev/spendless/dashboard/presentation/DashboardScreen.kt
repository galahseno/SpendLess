package id.dev.spendless.dashboard.presentation

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.domain.model.transaction.LargestTransaction
import id.dev.spendless.core.presentation.add_transaction.AddTransactionScreenRoot
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.SpendLessFab
import id.dev.spendless.core.presentation.design_system.component.transaction.EmptyTransaction
import id.dev.spendless.core.presentation.design_system.gradientBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.export.ExportScreenRoot
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import id.dev.spendless.dashboard.presentation.component.DashboardSummary
import id.dev.spendless.dashboard.presentation.component.DashboardTopAppBar
import id.dev.spendless.dashboard.presentation.component.LatestTransaction
import id.dev.spendless.dashboard.presentation.util.formatTimestamp
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreenRoot(
    onNavigateToAllTransaction: () -> Unit,
    onNavigateToSettingScreen: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    DashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is DashboardAction.OnShowAllClick -> onNavigateToAllTransaction()
                is DashboardAction.OnSettingClick -> onNavigateToSettingScreen()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit
) {
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val exportSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            DashboardTopAppBar(
                username = state.username,
                onSettingClick = {
                    onAction(DashboardAction.OnSettingClick)
                },
                onExportClick = {
                    onAction(DashboardAction.OnExportClick)
                }
            )
        },
        floatingActionButton = {
            SpendLessFab(
                onClick = {
                    scope.launch {
                        onAction(DashboardAction.OnFABClick)
                    }
                }
            )
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(it)
                .background(gradientBackground),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.49f)
                    .padding(top = 64.dp + 8.dp, start = 12.dp, end = 12.dp)
                    .systemBarsPadding(),
            ) {
                DashboardSummary(
                    balance = state.balance,
                    negativeBalance = state.negativeBalance,
                    previousWeekSpend = state.previousWeekSpend,
                    largestTransaction = state.largestTransaction,
                    largestTransactionAllTime = state.largestTransactionCategoryAllTime,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(screenBackground)
            ) {
                if (state.latestTransactions.isNotEmpty()) {
                    LatestTransaction(
                        onShowAllClick = {
                            onAction(DashboardAction.OnShowAllClick)
                        },
                        onItemClick = {id ->
                            onAction(DashboardAction.OnItemTransactionClick(id))
                        },
                        allTransactions = state.latestTransactions,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp, start = 16.dp, end = 10.dp),
                    )
                } else {
                    EmptyTransaction(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }

            AnimatedVisibility(
                visible = state.showAddBottomSheet
            ) {
                AddTransactionScreenRoot(
                    sheetState = addSheetState,
                    onDismissRequest = {
                        scope.launch {
                            addSheetState.hide()
                        }.invokeOnCompletion {
                            if (!addSheetState.isVisible) {
                                onAction(DashboardAction.OnCloseBottomSheet)
                            }
                        }
                    }
                )
            }

            AnimatedVisibility(
                visible = state.showExportBottomSheet
            ) {
                ExportScreenRoot(
                    sheetState = exportSheetState,
                    onDismissRequest = {
                        scope.launch {
                            exportSheetState.hide()
                        }.invokeOnCompletion {
                            if (!exportSheetState.isVisible) {
                                onAction(DashboardAction.OnCloseBottomSheet)
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
private fun DashboardScreenPreview() {
    SpendLessTheme {
        DashboardScreen(
            state = DashboardState(
                username = "rockefeller74",
                largestTransaction = LargestTransaction(
                    transactionName = "Adobe Yearly",
                    amount = "-59.99".formatTotalSpend(
                        expensesFormat = ExpensesFormatEnum.MinusPrefix,
                        currency = CurrencyEnum.USD,
                        decimal = DecimalSeparatorEnum.Dot,
                        thousands = ThousandsSeparatorEnum.Comma
                    ),
                    createdAt = System.currentTimeMillis().formatTimestamp(),
                )
            ),
            onAction = {}
        )
    }
}