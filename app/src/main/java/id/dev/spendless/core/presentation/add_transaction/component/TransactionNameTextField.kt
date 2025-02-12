package id.dev.spendless.core.presentation.add_transaction.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.errorBackground
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum

@Composable
fun TransactionNameTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    hintTransactionType: TransactionTypeEnum,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChanged,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .onFocusChanged { isFocused = it.isFocused }
                .defaultMinSize(minHeight = 24.dp, minWidth = 64.dp)
                .background(Color.Transparent),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            ),
            cursorBrush = SolidColor(buttonBackground),
            decorationBox = { innerBox ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (text.isEmpty() && !isFocused) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = when(hintTransactionType) {
                                TransactionTypeEnum.Expenses -> stringResource(R.string.receiver)
                                TransactionTypeEnum.Income -> stringResource(R.string.sender)
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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
private fun TransactionNameTextFieldPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(30.dp)
                .background(screenBackground),
            contentAlignment = Alignment.Center
        ) {
            TransactionNameTextField(
                text = "",
                onTextChanged = {},
                hintTransactionType = TransactionTypeEnum.Expenses,
                supportingText = "Username must be unique"
            )
        }
    }
}