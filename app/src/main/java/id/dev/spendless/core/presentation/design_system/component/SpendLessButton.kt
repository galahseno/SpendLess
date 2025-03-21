package id.dev.spendless.core.presentation.design_system.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.screenBackground

@Composable
fun SpendLessButton(
    text: String,
    onClick: () -> Unit,
    enable: Boolean,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    Button(
        enabled = enable,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = buttonBackground)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.W600),
            modifier = Modifier
                .padding(vertical = 6.dp)
        )
        if (icon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            icon()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SpendLessButtonPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .width(325.dp)
                .height(150.dp)
                .background(screenBackground)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            SpendLessButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Log in",
                onClick = {},
                enable = false,
                icon = {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = "",
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}