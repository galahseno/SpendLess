package id.dev.spendless.settings.presentation.security

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.screenBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityAppBar() {

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.security),
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