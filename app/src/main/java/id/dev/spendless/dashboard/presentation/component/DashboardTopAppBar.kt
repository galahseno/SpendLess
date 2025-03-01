package id.dev.spendless.dashboard.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.gradientBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopAppBar(
    username: String,
    onSettingClick: () -> Unit,
    // TODO Open Export Screen
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = username,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White
        ),
        actions = {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .clickable {

                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.download),
                    contentDescription = "export",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .padding(end = 14.dp)
                    .size(44.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.12f))
                    .clickable {
                        
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.setting_icon),
                    contentDescription = "export",
                    tint = Color.White,
                    modifier = Modifier
                        .clickable {
                            onSettingClick()
                        }
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TopAppBarDashboardPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier.background(gradientBackground)
        ) {
            DashboardTopAppBar(
                username = "rockefeller74",
                onSettingClick = {

                }
            )
        }
    }
}