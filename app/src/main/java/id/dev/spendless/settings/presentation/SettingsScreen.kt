package id.dev.spendless.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.logOutColor
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.design_system.settingIconsBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingScreenRoot(

    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    SettingScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun SettingScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {

    Scaffold(
        topBar = { SettingAppBar() }
    ) { innerPadding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .background(screenBackground)

        ) {

            Card(
                shape = RoundedCornerShape(
                    15.dp
                ),
                elevation = CardDefaults.cardElevation(
                    5.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .height(120.dp)
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(settingIconsBackground)
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
                        fontFamily = FontFamily(Font(R.font.figtree_medium))
                    )


                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(settingIconsBackground)
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
                        fontFamily = FontFamily(Font(R.font.figtree_medium))
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(
                    15.dp
                ),
                elevation = CardDefaults.cardElevation(
                    5.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier
                    .height(50.dp)
                    .padding(horizontal = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(settingIconsBackground)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.logout_icon),
                            contentDescription = "Settings_icon",
                            tint = logOutColor,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.log_out),
                        color = logOutColor,
                        fontFamily = FontFamily(Font(R.font.figtree_medium))
                    )


                }

            }

        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingAppBar() {

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.Settings),
                fontFamily = FontFamily(Font(R.font.figtree_semibold)),
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp)
            )
        },
        navigationIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.arrow_back),
                contentDescription = "navigate_Back"
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = screenBackground
        ),

        )

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