package id.dev.spendless.core.presentation.add_transaction.component

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.buttonBackground
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.keyPadBackground
import id.dev.spendless.core.presentation.ui.transaction.TransactionCategoryEnum
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCategoryDropDown(
    selectedExpenseCategory: TransactionCategoryEnum,
    onSelectedExpense: (TransactionCategoryEnum) -> Unit,
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
        ),
        shape = RoundedCornerShape(16.dp)
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
                    .padding(start = 4.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedExpenseCategory.categoryEmoji,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(keyPadBackground)
                        .padding(8.dp)
                )
                Text(
                    text = selectedExpenseCategory.categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }

            MaterialTheme(
                shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp)),
            ) {
                // TODO change the height and add scroll bar same as project overview
                DropdownMenu(
                    modifier = Modifier
                        .size(width = 380.dp, height = 240.dp)
                        .background(componentBackground),
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    TransactionCategoryEnum.entries.filter { it.transactionType == TransactionTypeEnum.Expenses }
                        .forEach { item ->
                            DropdownMenuItem(
                                leadingIcon = {
                                    Text(
                                        text = item.categoryEmoji,
                                        fontSize = 20.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(keyPadBackground)
                                    )
                                },
                                text = { Text(text = item.categoryName) },
                                onClick = {
                                    onSelectedExpense(item)
                                    expanded = false
                                },
                                trailingIcon = {
                                    if (selectedExpenseCategory.categoryName == item.categoryName) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = "selected_expense",
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
private fun ExpenseCategoryDropDownPreview() {
    SpendLessTheme {
        ExpenseCategoryDropDown(
            selectedExpenseCategory = TransactionCategoryEnum.Other,
            modifier = Modifier.padding(20.dp),
            onSelectedExpense = {

            }
        )
    }
}