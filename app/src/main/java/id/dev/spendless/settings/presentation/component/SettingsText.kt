package id.dev.spendless.settings.presentation.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import id.dev.spendless.R


@Composable
fun SettingText(modifier: Modifier = Modifier,text:String) {

    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight(500),
        fontFamily = FontFamily(Font(R.font.figtree_semibold)),
        modifier = modifier
    )

}