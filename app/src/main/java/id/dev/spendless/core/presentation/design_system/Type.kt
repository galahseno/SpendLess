package id.dev.spendless.core.presentation.design_system

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import id.dev.spendless.R

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.figtree_semibold)),
        fontWeight = FontWeight.W600,
        fontSize =  45.sp,
        lineHeight = 52.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.figtree_semibold)),
        fontWeight = FontWeight.W600,
        fontSize =  28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.figtree_regular)),
        fontWeight = FontWeight.W600,
        fontSize = 36.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.figtree_regular)),
        fontWeight = FontWeight.W400,
        fontSize = 16.sp
    ),
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)