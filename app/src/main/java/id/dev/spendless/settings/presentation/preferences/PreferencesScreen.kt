package id.dev.spendless.settings.presentation.preferences

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.SpendLessButton
import id.dev.spendless.core.presentation.design_system.component.SpendLessErrorContainer
import id.dev.spendless.core.presentation.design_system.component.preferences.CurrencyDropDown
import id.dev.spendless.core.presentation.design_system.component.preferences.DecimalSeparator
import id.dev.spendless.core.presentation.design_system.component.preferences.ExpensesFormat
import id.dev.spendless.core.presentation.design_system.component.preferences.ThousandSeparator
import id.dev.spendless.core.presentation.design_system.component.preferences.TotalSpendCard
import id.dev.spendless.core.presentation.design_system.errorHeightClosedKeyboard
import id.dev.spendless.core.presentation.design_system.errorHeightOpenKeyboard
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.core.presentation.ui.formatTotalSpend
import id.dev.spendless.core.presentation.ui.keyboardOpenAsState
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum
import id.dev.spendless.settings.presentation.SettingsAction
import id.dev.spendless.settings.presentation.SettingsEvent
import id.dev.spendless.settings.presentation.SettingsState
import id.dev.spendless.settings.presentation.SettingsViewModel
import id.dev.spendless.settings.presentation.component.SettingsTopAppBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreferencesScreenRoot(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is SettingsEvent.OnSuccessSavePreferences -> onBackClick()
        }
    }

    PreferencesScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OnBackClick -> onBackClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun PreferencesScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {
    val keyboardOpen by keyboardOpenAsState()
    val errorHeightDp by animateDpAsState(
        if (keyboardOpen) errorHeightOpenKeyboard else errorHeightClosedKeyboard,
        label = "anim_error_height"
    )

    Scaffold(
        topBar = {
            SettingsTopAppBar(
                titleAppBar = stringResource(R.string.preferences),
                modifier = Modifier,
                onBackClick = {
                    onAction(SettingsAction.OnBackClick)
                }
            )
        },
        containerColor = screenBackground,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TotalSpendCard(
                    totalSpend = state.formattedTotalSpend,
                    modifier = Modifier.height(110.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.expenses_format),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp
                    )
                )
                ExpensesFormat(
                    selectedExpenseFormat = state.selectedExpenseFormat,
                    currency = state.selectedCurrency.symbol,
                    onExpensesSelected = {
                        onAction(SettingsAction.OnExpensesFormatSelected(it))
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.currency_format),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp
                    )
                )
                CurrencyDropDown(
                    selectedCurrency = state.selectedCurrency,
                    onSelectedCurrency = {
                        onAction(SettingsAction.OnCurrencySelected(it))
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.desimal_separator),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp
                    )
                )
                DecimalSeparator(
                    selectedSeparator = state.selectedDecimalSeparator,
                    onExpensesSelected = {
                        onAction(SettingsAction.OnDecimalSeparatorSelected(it))
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.thousand_separator),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.W500,
                        fontSize = 14.sp
                    )
                )
                ThousandSeparator(
                    selectedSeparator = state.selectedThousandSeparator,
                    onExpensesSelected = {
                        onAction(SettingsAction.OnThousandSeparatorSelected(it))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                SpendLessButton(
                    modifier = Modifier.fillMaxWidth(),
                    enable = state.canSave,
                    text = stringResource(R.string.save),
                    onClick = {
                        onAction(SettingsAction.OnSavePreferences)
                    }
                )
            }

            SpendLessErrorContainer(
                isErrorVisible = state.isErrorVisible,
                errorHeightDp = errorHeightDp,
                errorMessage = state.errorMessage?.asString() ?: "",
                keyboardOpen = keyboardOpen,
                modifier = Modifier
                    .imePadding()
                    .align(Alignment.BottomCenter)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreferencesScreenPreview() {
    SpendLessTheme {
        PreferencesScreen(
            state = SettingsState(
                formattedTotalSpend = "-10382.45".formatTotalSpend(
                    expensesFormat = ExpensesFormatEnum.MinusPrefix,
                    currency = CurrencyEnum.USD,
                    decimal = DecimalSeparatorEnum.Dot,
                    thousands = ThousandsSeparatorEnum.Comma
                )
            ),
            onAction = {}
        )
    }


}