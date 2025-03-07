package id.dev.spendless.core.presentation.design_system.component.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.dev.spendless.R
import id.dev.spendless.core.domain.model.transaction.Transaction
import id.dev.spendless.core.domain.model.transaction.TransactionGroup
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.componentBackground
import id.dev.spendless.core.presentation.design_system.emojiIncomeBackground
import id.dev.spendless.core.presentation.design_system.incomeBackground
import id.dev.spendless.core.presentation.design_system.keyPadBackground
import id.dev.spendless.core.presentation.design_system.noteExpenseColor
import id.dev.spendless.core.presentation.design_system.noteIncomeColor
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransactionLazyList(
    allTransactions: List<TransactionGroup>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        allTransactions.forEach { transactionGroup ->
            stickyHeader {
                Text(
                    text = transactionGroup.formattedDate.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W500,
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .background(screenBackground)
                )
            }
            items(
                transactionGroup.transactions,
                key = { it.id }
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .then(
                            if (it.isNoteOpen) {
                                Modifier

                                    .background(componentBackground)
                            } else Modifier
                        )
                        .then(
                            if (it.note.isNotEmpty()) {
                                Modifier
                                    .clickable {
                                        onItemClick(it.id)
                                    }
                            } else Modifier
                        )
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, end = 16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                        ) {
                            Text(
                                text = it.categoryEmoji,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (it.categoryName == TransactionTypeEnum.Income.name)
                                            emojiIncomeBackground
                                        else
                                            keyPadBackground
                                    )
                                    .padding(8.dp)
                            )
                            if (it.note.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 4.dp, y = 4.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(componentBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.note_icon),
                                        contentDescription = "notes_icon",
                                        tint = if (it.categoryName == TransactionTypeEnum.Income.name)
                                            noteIncomeColor
                                        else
                                            noteExpenseColor
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = it.transactionName,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.W500,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = it.categoryName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 12.sp,
                                )
                            )
                        }
                        Text(
                            text = it.amount,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 20.sp,
                                color = if (it.categoryName == TransactionTypeEnum.Income.name)
                                    incomeBackground else Color.Black
                            )
                        )
                    }
                    if (it.note.isNotEmpty()) {
                        AnimatedVisibility(
                            it.isNoteOpen
                        ) {
                            Text(
                                text = it.note,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 14.sp,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 55.dp, end = 12.dp),
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
private fun TransactionLazyListPreview() {
    SpendLessTheme {
        TransactionLazyList(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackground)
                .padding(10.dp),
            onItemClick = {},
            allTransactions = generateTransactionGroups(),
        )
    }
}

fun generateTransactionGroups(): List<TransactionGroup> {
    val transactionGroups = ArrayList<TransactionGroup>()

    val today = LocalDate.now()
    val todayTransactions = ArrayList<Transaction>()
    todayTransactions.add(
        Transaction(
            1,
            "Lunch",
            "🍔",
            "Food",
            "-$10.00",
            "",
            today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    )
    todayTransactions.add(
        Transaction(
            2,
            "Coffee",
            "☕",
            "Income",
            "-$5.00",
            "NotesNotesNotesNotesNotesNotesNotesNotesNotes",
            today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    )
    transactionGroups.add(TransactionGroup("Today", todayTransactions))

    val yesterday = today.minusDays(1)
    val yesterdayTransactions = ArrayList<Transaction>()
    yesterdayTransactions.add(
        Transaction(
            3,
            "Groceries",
            "🛒",
            "Shopping",
            "-$20.00",
            "Notes",
            yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    )
    transactionGroups.add(TransactionGroup("Yesterday", yesterdayTransactions))

    val threeDaysAgo = today.minusDays(3)
    val threeDaysAgoTransactions = ArrayList<Transaction>()
    threeDaysAgoTransactions.add(
        Transaction(
            4,
            "Movie",
            "🍿",
            "Entertainment",
            "-$15.00",
            "Notes",
            threeDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    )
    transactionGroups.add(
        TransactionGroup(
            threeDaysAgo.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
            threeDaysAgoTransactions
        )
    )

    val fiveDaysAgo = today.minusDays(5)
    val fiveDaysAgoTransactions = ArrayList<Transaction>()
    fiveDaysAgoTransactions.add(
        Transaction(
            5,
            "Book",
            "📚",
            "Books",
            "-$10.00",
            "Notes",
            fiveDaysAgo.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    )
    transactionGroups.add(
        TransactionGroup(
            fiveDaysAgo.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
            fiveDaysAgoTransactions
        )
    )

    return transactionGroups
}