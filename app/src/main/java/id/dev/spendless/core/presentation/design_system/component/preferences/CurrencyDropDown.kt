package id.dev.spendless.core.presentation.design_system.component.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    selectedCurrency: CurrencyEnum,
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
            Row(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxSize()
                    .padding(end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedCurrency.symbol,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .padding(start = 14.dp)
                )
                Text(
                    text = selectedCurrency.currencyName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }

            MaterialTheme(
                shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
            ) {
                // TODO change the height and add scroll bar same as project overview
                DropdownMenu(
                    modifier = Modifier
                        .size(width = 380.dp, height = 240.dp)
                        .background(componentBackground),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    CurrencyEnum.entries.forEach { item ->
                        DropdownMenuItem(
                            leadingIcon = {
                                Text(
                                    text = item.symbol,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center,
                                )
                            },
                            text = { Text(text = item.currencyName) },
                            onClick = {
                                onSelectedCurrency(item)
                                expanded = false
                            },
                            trailingIcon = {
                                if (selectedCurrency.symbol == item.symbol) {
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
            selectedCurrency = CurrencyEnum.IDR,
            modifier = Modifier.padding(20.dp),
            onSelectedCurrency = {

            }
        )
    }
}