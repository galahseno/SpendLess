package id.dev.spendless.settings.presentation

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.errorBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.design_system.sheetBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.settings.presentation.component.SettingsTopAppBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingScreenRoot(
    onBackClick: () -> Unit,
    onPreferencesClick: () -> Unit,
    onSecurityClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val view = LocalView.current

    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
    }

    ObserveAsEvents(viewModel.event) { event ->

    }

    SettingScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SettingsAction.OnBackClick -> onBackClick()
                is SettingsAction.OnPreferencesClick -> onPreferencesClick()
                is SettingsAction.OnSecurityClick -> onSecurityClick()

                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun SettingScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    Scaffold(
        topBar = {
            SettingsTopAppBar(
                titleAppBar = stringResource(R.string.settings),
                modifier = Modifier,
                onBackClick = {
                    onAction(SettingsAction.OnBackClick)
                }
            )
        },
        containerColor = screenBackground,
    ) { innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = componentBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onAction(SettingsAction.OnPreferencesClick)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(sheetBackground)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.setting_icon),
                            contentDescription = "Settings_icon",
                            Modifier.padding(10.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.preferences),
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.W500
                        )
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onAction(SettingsAction.OnSecurityClick)
                        }
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(sheetBackground)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.lock_icon),
                            contentDescription = "Security_icon",
                            Modifier.padding(10.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.security),
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.W500
                        )
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = componentBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onAction(SettingsAction.OnLogoutClick)
                        }
                ) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(errorBackground.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.logout_icon),
                            contentDescription = "Settings_icon",
                            tint = errorBackground,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.log_out),
                        color = errorBackground,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.W500
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SpendLessTheme {
        SettingScreen(
            state = SettingsState(),
            onAction = {}
        )
    }
}