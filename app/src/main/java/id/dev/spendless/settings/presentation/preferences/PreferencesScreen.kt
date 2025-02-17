package id.dev.spendless.settings.presentation.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.settings.presentation.SettingsAction
import id.dev.spendless.settings.presentation.SettingsState
import id.dev.spendless.settings.presentation.SettingsViewModel
import id.dev.spendless.settings.presentation.component.MultipleSegmentedButtonsCreator
import id.dev.spendless.settings.presentation.component.SettingText
import org.koin.androidx.compose.koinViewModel

@Composable
fun PreferencesScreenRoot(

    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    PreferencesScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OnSelectionOfCurrency -> {
                    // new currency selected
                }

                is SettingsAction.OnSelectionOfExpensesFormat -> TODO()
                else -> {}
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreferencesScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
) {

    Scaffold(
        topBar = { PreferencesAppBar() }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .height(542.dp)
        ) {
            Card(
                shape = RoundedCornerShape(15.dp),
                elevation = CardDefaults.cardElevation(5.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 24.dp)
                    .height(110.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "-$28,212.32",
                        fontSize = 32.sp,
                        fontFamily = FontFamily(Font(R.font.figtree_semibold)),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight(600),
                        lineHeight = 40.sp

                    )

                    Text(
                        text = stringResource(R.string.spend_this_month),
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily(Font(R.font.figtree_regular)),
                        fontWeight = FontWeight(400),
                        lineHeight = 20.sp

                    )
                }


            }


            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(336.dp)
                    .fillMaxWidth()
                    .background(screenBackground)

            ) {

                Column(
                    modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                ) {

                    SettingText(
                        text = stringResource(R.string.expenses_format)
                    )

                    MultipleSegmentedButtonsCreator(
                        list = listOf("-$10", "($10)"),
                        selectedIndex = 0
                    )

                }

                Spacer(
                    Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                )

                PreferencesCurrencySelector(
                    currentSelectedCurrency = state.selectedCurrencyEnum,
                    onSelectionOfCurrencyEnum = {
                        onAction(SettingsAction.OnSelectionOfCurrency(it))
                    }
                )

                Spacer(
                    Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                )

                Column(
                    modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                ) {

                    SettingText(text = stringResource(R.string.desimal_separator))

                    MultipleSegmentedButtonsCreator(
                        list = listOf("1.00", "1,00"),
                        selectedIndex = 0
                    )

                }

                Spacer(
                    Modifier
                        .height(10.dp)
                        .fillMaxWidth()
                )
                Column(
                    modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                ) {
                    SettingText(text = stringResource(R.string.thousand_separator))

                    MultipleSegmentedButtonsCreator(
                        list = listOf("1.000", "1,000", "1 000"),
                        selectedIndex = 0
                    )

                }


            }



            Button(
                onClick = {},
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 8.dp)

            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
private fun PreferencesScreenPreview() {
    SpendLessTheme {
        PreferencesScreen(
            state = SettingsState(),
            onAction = {}
        )
    }


}