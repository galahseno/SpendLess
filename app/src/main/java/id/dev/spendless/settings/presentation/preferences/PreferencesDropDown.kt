package id.dev.spendless.settings.presentation.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.ui.preferences.CurrencyEnum
import id.dev.spendless.settings.presentation.component.SettingText


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesCurrencySelector(
    modifier: Modifier = Modifier,
    currentSelectedCurrency: CurrencyEnum,
    onSelectionOfCurrencyEnum: (CurrencyEnum) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .height(72.dp)
            .fillMaxWidth()
    ) {

        SettingText(text = stringResource(R.string.currency_format))

        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(5.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.height(48.dp)
        ) {

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                Row(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxSize()
                        .padding(end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = currentSelectedCurrency.symbol,
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
                        text = currentSelectedCurrency.currencyName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }


                MaterialTheme(
                    shapes = MaterialTheme.shapes.copy(
                        extraSmall = RoundedCornerShape(16.dp)
                    )
                ) {
                    DropdownMenu(
                        modifier = Modifier
                            .exposedDropdownSize()
                            .background(Color.White),
                        expanded = expanded,
                        onDismissRequest = { expanded = false },

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
                                    onSelectionOfCurrencyEnum(item)
                                    expanded = false
                                },
                                trailingIcon = {
                                    if (currentSelectedCurrency.symbol == item.symbol) {
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
}