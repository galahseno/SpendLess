package id.dev.spendless.core.presentation.design_system.component.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropDown(
    selectedSymbolCurrency: String,
    selectedCurrencyName: String,
    onSelectedCurrency: (CurrencyEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = CardDefaults.cardColors(
            containerColor = componentBackground,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.titleMedium,
                value = selectedCurrencyName,
                onValueChange = {},
                readOnly = true,
                leadingIcon = {
                    Text(
                        text = selectedSymbolCurrency,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500
                        ),
                    )
                },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxSize()
                    .menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = componentBackground,
                    unfocusedContainerColor = componentBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            MaterialTheme(
                shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
            ) {
                DropdownMenu(
                    modifier = Modifier
                        .exposedDropdownSize()
                        .background(componentBackground),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CurrencyEnum.entries.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.currencyName) },
                            onClick = {
                                onSelectedCurrency(item)
                                expanded = false
                            },
                            trailingIcon = {
                                if (selectedSymbolCurrency == item.symbol) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = "selected_currency",
                                        tint = buttonBackground
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyDropDownPreview() {
    SpendLessTheme {
        CurrencyDropDown(
            selectedSymbolCurrency = CurrencyEnum.IDR.symbol,
            selectedCurrencyName = CurrencyEnum.IDR.currencyName,
            modifier = Modifier.padding(20.dp),
            onSelectedCurrency = {

            }
        )
    }
}