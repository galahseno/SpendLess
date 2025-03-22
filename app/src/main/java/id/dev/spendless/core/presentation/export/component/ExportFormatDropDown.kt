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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import id.dev.spendless.core.domain.model.export.ExportFormatEnum
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.ui.crop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportFormatDropDown(
    selectedFormat: ExportFormatEnum,
    onSelectedFormat: (ExportFormatEnum) -> Unit,
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
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedFormat.name,
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
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    DropdownMenu(
                        modifier = Modifier
                            .width(with(LocalDensity.current) { maxWidth })
                            .sizeIn(maxHeight = 205.dp)
                            .background(componentBackground)
                            .crop(vertical = 8.dp),
                        expanded = expanded,
                        offset = DpOffset(x = 0.dp, y = maxHeight + 6.dp ),
                        onDismissRequest = { expanded = false }
                    ) {
                        ExportFormatEnum.entries.forEach { item ->
                            DropdownMenuItem(
                                contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
                                text = {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                },
                                onClick = {
                                    onSelectedFormat(item)
                                    expanded = false
                                },
                                trailingIcon = {
                                    if (selectedFormat.name == item.name) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = "selected_format",
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

@Preview(showBackground = true)
@Composable
private fun ExportFormatDropDownPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier.size(400.dp),
            contentAlignment = Alignment.Center
        ) {
            ExportFormatDropDown(
                selectedFormat = ExportFormatEnum.CSV,
                modifier = Modifier.padding(20.dp),
                onSelectedFormat = {

                }
            )
        }
    }
}