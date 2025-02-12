package id.dev.spendless.auth.presentation.register.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.errorBackground
import id.dev.spendless.core.presentation.design_system.registerTextFieldBackground
import java.util.Locale

@Composable
fun RegisterTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChanged,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused }
                .defaultMinSize(minHeight = 64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(registerTextFieldBackground),
            textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
            cursorBrush = SolidColor(buttonBackground),
            decorationBox = { innerBox ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (text.isEmpty() && !isFocused) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = hint.lowercase(Locale.ROOT),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                            textAlign = TextAlign.Center
                        )
                    }
                    innerBox()
                }
            }
        )
        supportingText?.let {
            Spacer(modifier = Modifier.height(2.dp))
            if (text.isNotEmpty()) {
                Text(
                    text = it,
                    color = errorBackground,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterTextFieldPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .width(325.dp)
                .height(70.dp)
                .background(buttonBackground),
            contentAlignment = Alignment.Center
        ) {
            RegisterTextField(
                text = "",
                onTextChanged = {},
                hint = "Username",
                supportingText = "Username must be unique"
            )
        }
    }
}