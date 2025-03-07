package id.dev.spendless.auth.presentation.register.onboarding_preferences

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.auth.presentation.register.RegisterAction
import id.dev.spendless.auth.presentation.register.RegisterEvent
import id.dev.spendless.auth.presentation.register.RegisterState
import id.dev.spendless.auth.presentation.register.RegisterViewModel
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
import id.dev.spendless.core.presentation.design_system.topPaddingAuthScreen
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.core.presentation.ui.keyboardOpenAsState

@Composable
fun OnboardingPreferencesScreenRoot(
    onBackClick: () -> Unit,
    onSuccessRegister: () -> Unit,
    viewModel: RegisterViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is RegisterEvent.OnRegisterSuccess -> onSuccessRegister()
            else -> Unit
        }
    }

    BackHandler {
        viewModel.onAction(RegisterAction.OnResetPin)
        onBackClick()
    }

    OnboardingPreferencesScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RegisterAction.OnBackClick -> {
                    viewModel.onAction(RegisterAction.OnResetPin)
                    onBackClick()
                }

                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OnboardingPreferencesScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit
) {
    val keyboardOpen by keyboardOpenAsState()
    val errorHeightDp by animateDpAsState(
        if (keyboardOpen) errorHeightOpenKeyboard else errorHeightClosedKeyboard,
        label = "anim_error_height"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
            .then(
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                    Modifier.systemBarsPadding()
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = topPaddingAuthScreen)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.set_spendless_preferences),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = stringResource(R.string.you_can_change_anytime_in_setting),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(14.dp))
            TotalSpendCard(
                totalSpend = state.formattedTotalSpend,
                modifier = Modifier.height(106.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
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
                    onAction(RegisterAction.OnExpensesFormatSelected(it))
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
                    onAction(RegisterAction.OnCurrencySelected(it))
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
                onSeparatorSelected = {
                    onAction(RegisterAction.OnDecimalSeparatorSelected(it))
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
                onSeparatorSelected = {
                    onAction(RegisterAction.OnThousandSeparatorSelected(it))
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            SpendLessButton(
                modifier = Modifier.fillMaxWidth(),
                enable = state.canProsesRegister,
                text = stringResource(R.string.start_tracking),
                onClick = {
                    onAction(RegisterAction.OnRegisterAccount)
                }
            )
        }

        TopAppBar(
            modifier = Modifier
                .padding(start = 10.dp, top = 35.dp),
            title = {},
            navigationIcon = {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "back_icon",
                    modifier = Modifier.clickable {
                        onAction(RegisterAction.OnBackClick)
                    }
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

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

@Preview(showBackground = true)
@Composable
private fun OnboardingPreferencesScreenPreview() {
    SpendLessTheme {
        OnboardingPreferencesScreen(
            state = RegisterState(
                canProsesRegister = true
            ),
            onAction = {}
        )
    }
}