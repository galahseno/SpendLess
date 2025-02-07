package id.dev.spendless.auth.presentation.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.errorBackground
import id.dev.spendless.core.presentation.design_system.screenBackground

@Composable
fun LoginTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    TextField(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        value = text,
        onValueChange = onTextChanged,
        placeholder = {
            Text(
                text = hint,
                style = MaterialTheme.typography.titleMedium
            )
        },
        textStyle = MaterialTheme.typography.titleMedium,
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
        visualTransformation = visualTransformation,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = componentBackground,
            unfocusedContainerColor = componentBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginTextFieldPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .width(325.dp)
                .height(150.dp)
                .background(screenBackground),
            contentAlignment = Alignment.Center
        ) {
            LoginTextField(
                text = "",
                onTextChanged = {},
                hint = "Username",
            )
        }
    }
}