package id.dev.spendless.core.presentation.design_system

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val screenBackground = Color(0xFFFEF7FF)
val componentBackground = Color(0xFFFFFFFF)
val buttonBackground = Color(0xFF5A00C8)
val errorBackground = Color(0xFFA40019)
val registerTextFieldBackground = Color(0xFFEBE5EE)
val keyPadBackground = Color(0xFFEADDFF)
val secondaryColor = Color(0xFFE5EA58)

val gradientBackground = Brush.radialGradient(
    colors = listOf(
        Color(0xFF5A00C8),
        Color(0xFF24005A),
    ),
    center = Offset(x = 0f, y = 1f),
    radius = 1250f
)
