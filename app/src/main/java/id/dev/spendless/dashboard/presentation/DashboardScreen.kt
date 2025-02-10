package id.dev.spendless.dashboard.presentation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.SpendLessFab
import id.dev.spendless.core.presentation.design_system.component.transaction.EmptyTransaction
import id.dev.spendless.core.presentation.design_system.gradientBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.dashboard.presentation.component.DashboardSummary
import id.dev.spendless.dashboard.presentation.component.LatestTransaction
import id.dev.spendless.dashboard.presentation.component.TopAppBarDashboard
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreenRoot(
    onNavigateToAllTransaction: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    ObserveAsEvents(viewModel.event) {

    }

    DashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is DashboardAction.OnShowAllClick -> onNavigateToAllTransaction()
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarDashboard(
                username = state.username,
            )
        },
        floatingActionButton = {
            SpendLessFab(
                onClick = {

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
                    .fillMaxHeight(0.45f)
                    .padding(top = 64.dp + 8.dp, start = 12.dp, end = 12.dp)
                    .systemBarsPadding(),
            ) {
                DashboardSummary(
                    balance = state.balance,
                    previousWeekSpend = state.previousWeekSpend,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.53f)
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(screenBackground)
            ) {
                if (state.transaction) {
                    LatestTransaction(
                        onShowAllClick = {
                            onAction(DashboardAction.OnShowAllClick)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    )
                } else {
                    EmptyTransaction(
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
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
                transaction = false
            ),
            onAction = {}
        )
    }
}