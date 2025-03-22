package id.dev.spendless.core.presentation.export.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import id.dev.spendless.core.domain.model.export.ExportRangeEnum
import id.dev.spendless.core.domain.model.export.MonthYear
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.sheetBackground
import id.dev.spendless.core.presentation.ui.crop
import id.dev.spendless.core.presentation.ui.toDisplayString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportRangeDropDown(
    months: List<MonthYear>,
    selectedRange: ExportRangeEnum,
    selectedMonth: MonthYear,
    onSelectedRange: (ExportRangeEnum) -> Unit,
    onSelectedMonth: (MonthYear) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var showMonthsMenu by rememberSaveable { mutableStateOf(false) }

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
            onExpandedChange = { expanded = it }
        ) {
            Row(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (selectedRange == ExportRangeEnum.SPECIFIC_MONTH) {
                        selectedMonth.toDisplayString()
                    } else {
                        selectedRange.displayName
                    },
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    textAlign = TextAlign.Start
                )
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }

            MaterialTheme(
                shapes = MaterialTheme.shapes.copy(
                    extraSmall = RoundedCornerShape(16.dp)
                )
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    DropdownMenu(
                        modifier = Modifier
                            .width(with(LocalDensity.current) { maxWidth })
                            .sizeIn(maxHeight = 205.dp)
                            .background(componentBackground)
                            .crop(vertical = 8.dp),
                        expanded = expanded,
                        offset = DpOffset(x = 0.dp, y = maxHeight + 6.dp),
                        onDismissRequest = {
                            expanded = false
                            showMonthsMenu = false
                        }
                    ) {
                        if (showMonthsMenu) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .background(sheetBackground),
                                contentPadding = PaddingValues(8.dp),
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(componentBackground)
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = "back",
                                        )
                                    }
                                },
                                text = {
                                    Text(
                                        text = ExportRangeEnum.SPECIFIC_MONTH.displayName,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                },
                                onClick = {
                                    showMonthsMenu = false
                                },
                            )
                            months.forEach { month ->
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
                                    text = {
                                        Text(
                                            text = month.toDisplayString(),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    },
                                    onClick = {
                                        onSelectedMonth(month)
                                        onSelectedRange(ExportRangeEnum.SPECIFIC_MONTH)
                                        expanded = false
                                        showMonthsMenu = false
                                    },
                                    trailingIcon = {
                                        if (month == selectedMonth) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "selected_month",
                                                tint = buttonBackground
                                            )
                                        }
                                    }
                                )
                            }
                        } else {
                            ExportRangeEnum.entries.forEach { item ->
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
                                    text = {
                                        Text(
                                            text = item.displayName,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    },
                                    onClick = {
                                        if (item == ExportRangeEnum.SPECIFIC_MONTH) {
                                            showMonthsMenu = true
                                        } else {
                                            onSelectedRange(item)
                                            expanded = false
                                        }
                                    },
                                    trailingIcon = {
                                        if (selectedRange == item) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "selected_range",
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
}

@Preview(showBackground = true)
@Composable
private fun ExportRangeDropDownPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier.size(400.dp),
            contentAlignment = Alignment.Center
        ) {
            ExportRangeDropDown(
                months = emptyList(),
                selectedRange = ExportRangeEnum.LAST_THREE_MONTHS,
                selectedMonth = MonthYear(2023, 1),
                onSelectedMonth = {},
                onSelectedRange = {},
            )
        }
    }
}