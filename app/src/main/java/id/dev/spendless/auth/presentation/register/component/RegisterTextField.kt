package id.dev.spendless.auth.presentation.register.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.errorBackground
import id.dev.spendless.core.presentation.design_system.registerTextFieldBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
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
    TextField(
        modifier = modifier.height(95.dp),
        shape = RoundedCornerShape(16.dp),
        value = text,
        onValueChange = onTextChanged,
        placeholder = {
            Text(
                text = hint.lowercase(Locale.ROOT),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        textStyle = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
        supportingText = {
            supportingText?.let {
                if (text.isNotEmpty()) {
                    Text(
                        text = it,
                        color = errorBackground,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = registerTextFieldBackground,
            unfocusedContainerColor = registerTextFieldBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun RegisterTextFieldPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .width(325.dp)
                .height(150.dp)
                .background(screenBackground)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            RegisterTextField(
                text = "",
                onTextChanged = {},
                hint = "Username",
            )
        }
    }
}