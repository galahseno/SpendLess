package id.dev.spendless.core.presentation.design_system.component.pin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.keyPadBackground

@Composable
fun KeyPad(
    onNumberPadClick: (String) -> Unit,
    onDeletePadClick: () -> Unit,
    onBiometricClick: () -> Unit,
    modifier: Modifier = Modifier,
    isBiometricEnabled: Boolean = false,
    padBackground: Color = keyPadBackground
) {
    val numberPadLayout = remember {
        mutableStateListOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "delete")
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        numberPadLayout.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp, alignment = CenterHorizontally)
            ) {
                row.forEach { number ->
                    when (number) {
                        "" -> BiometricButton(
                            isBiometricEnabled = isBiometricEnabled,
                            onBiometricClick = onBiometricClick,
                            keyPadBackground = padBackground
                        )

                        "delete" -> DeleteButton(
                            onDeletePadClick = onDeletePadClick,
                            keyPadBackground = padBackground
                        )

                        else -> NumberButton(
                            number = number,
                            onNumberPadClick = onNumberPadClick,
                            keyPadBackground = padBackground
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberButton(
    number: String,
    onNumberPadClick: (String) -> Unit,
    keyPadBackground: Color
) {
    KeyPadButton(
        onClick = { onNumberPadClick(number) },
        containerColor = keyPadBackground,
        content = {
            Text(
                text = number,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp)
            )
        }
    )
}

@Composable
private fun DeleteButton(
    onDeletePadClick: () -> Unit,
    keyPadBackground: Color
) {
    KeyPadButton(
        onClick = onDeletePadClick,
        containerColor = keyPadBackground.copy(alpha = 0.3f),
        content = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.delete_pad),
                contentDescription = "delete_pad"
            )
        }
    )
}

@Composable
private fun BiometricButton(
    isBiometricEnabled: Boolean,
    onBiometricClick: () -> Unit,
    keyPadBackground: Color
) {
    KeyPadButton(
        enabled = isBiometricEnabled,
        onClick = { if (isBiometricEnabled) onBiometricClick() },
        containerColor = if (isBiometricEnabled) keyPadBackground.copy(alpha = 0.3f)
        else Color.Transparent,
        disabledContainerColor = Color.Transparent,
        content = {
            if (isBiometricEnabled) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.biometric_pad),
                    contentDescription = "biometric_pad"
                )
            }
        }
    )
}

@Composable
private fun KeyPadButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color,
    disabledContainerColor: Color = containerColor,
    content: @Composable () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.Black,
            disabledContainerColor = disabledContainerColor
        ),
        shape = RoundedCornerShape(32.dp),
        modifier = modifier.size(108.dp)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun KeyPadPreview() {
    SpendLessTheme {
        KeyPad(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            onNumberPadClick = {},
            onDeletePadClick = {},
            onBiometricClick = {},
            isBiometricEnabled = true
        )
    }
}