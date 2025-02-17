package id.dev.spendless.settings.presentation.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.settings.presentation.SettingsAction
import id.dev.spendless.settings.presentation.SettingsState
import id.dev.spendless.settings.presentation.SettingsViewModel
import id.dev.spendless.settings.presentation.component.MultipleSegmentedButtonsCreator
import id.dev.spendless.settings.presentation.component.SettingText
import org.koin.androidx.compose.koinViewModel

@Composable
fun SecurityScreenRoot(

    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    SecurityScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecurityScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {

    Scaffold(
        topBar = { SecurityAppBar() }
    ) { innerPading ->

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPading)
                .fillMaxWidth()
                .height(320.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .height(72.dp)
                        .fillMaxWidth()
                ) {

                    SettingText(text = stringResource(R.string.biometric_for_pin_prompt))

                    MultipleSegmentedButtonsCreator(
                        list = listOf("Enable", "Disable"),
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

                    SettingText(text = stringResource(R.string.session_expiry_duration))

                    MultipleSegmentedButtonsCreator(
                        list = listOf("5 min", "15 min", "30 min", "1 hour"),
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
                ){

                    SettingText(text = stringResource(R.string.locked_out_duration))

                    MultipleSegmentedButtonsCreator(
                        list = listOf("15 sec", "30 sec", "1 min ", "5 min"),
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
                    text = "Save",
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
private fun SecurityScreenPreview() {
    SpendLessTheme {
        SecurityScreen(
            state = SettingsState(),
            onAction = {}
        )
    }
}