package id.dev.spendless.core.presentation.add_transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.core.presentation.add_transaction.component.AddTransactionAppBar
import id.dev.spendless.core.presentation.add_transaction.component.AddTransactionType
import id.dev.spendless.core.presentation.add_transaction.component.TransactionAmountTextField
import id.dev.spendless.core.presentation.add_transaction.component.TransactionNameTextField
import id.dev.spendless.core.presentation.add_transaction.component.TransactionNoteTextField
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.sheetBackground
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.core.presentation.ui.transaction.TransactionTypeEnum
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreenRoot(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onCloseModalSheet: () -> Unit,
    viewModel: AddTransactionViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) {

    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(0.935f),
        containerColor = sheetBackground,
        tonalElevation = 16.dp,
        sheetState = sheetState,
        shape = RoundedCornerShape(topEnd = 28.dp, topStart = 28.dp),
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        windowInsets = WindowInsets.waterfall
    ) {
        AddTransactionScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    is AddTransactionAction.OnCloseModalSheet -> onCloseModalSheet()
                    else -> {}
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Composable
private fun AddTransactionScreen(
    state: AddTransactionState,
    onAction: (AddTransactionAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { TransactionTypeEnum.entries.size })

    var firstOpenScreen by rememberSaveable { mutableStateOf(true) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val transactionNameFocus = remember { FocusRequester() }
    val amountFocus = remember { FocusRequester() }
    val noteFocus = remember { FocusRequester() }

    LaunchedEffect(firstOpenScreen) {
        if (firstOpenScreen) {
            transactionNameFocus.requestFocus()
            firstOpenScreen = false
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        onAction(
            AddTransactionAction.OnTransactionTypeSelected(
                TransactionTypeEnum.entries[pagerState.currentPage]
            )
        )
        keyboardController?.hide()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
    ) {
        AddTransactionAppBar(
            modifier = Modifier.padding(end = 12.dp),
            onCloseClick = {
                onAction(AddTransactionAction.OnCloseModalSheet)
            }
        )
        AddTransactionType(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp),
            selectedTransactionType = state.selectedTransactionType,
            onTransactionTypeSelected = {
                onAction(AddTransactionAction.OnTransactionTypeSelected(it))
                scope.launch {
                    pagerState.animateScrollToPage(TransactionTypeEnum.entries.indexOf(it))
                }
            }
        )

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 38.dp)
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TransactionNameTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(transactionNameFocus),
                    text = when (TransactionTypeEnum.entries[page]) {
                        TransactionTypeEnum.Expenses -> state.expenseName
                        TransactionTypeEnum.Income -> state.incomeName
                    },
                    hintTransactionType = TransactionTypeEnum.entries[page],
                    onTextChanged = { text ->
                        onAction(
                            AddTransactionAction.OnExpenseOrIncomeNameChanged(
                                TransactionTypeEnum.entries[page],
                                text
                            )
                        )
                    },
                    supportingText = when (TransactionTypeEnum.entries[page]) {
                        TransactionTypeEnum.Expenses -> {
                            if (state.expenseNameSupportText != null)
                                state.expenseNameSupportText.asString()
                            else null
                        }

                        TransactionTypeEnum.Income -> {
                            if (state.incomeNameSupportText != null)
                                state.incomeNameSupportText.asString()
                            else null
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onNext = {
                            amountFocus.requestFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                Spacer(modifier = Modifier.height(4.dp))
                TransactionAmountTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(amountFocus),
                    text = when (TransactionTypeEnum.entries[page]) {
                        TransactionTypeEnum.Expenses -> state.expenseAmount
                        TransactionTypeEnum.Income -> state.incomeAmount
                    },
                    onTextChanged = { text ->
                        onAction(
                            AddTransactionAction.OnExpenseOrIncomeAmountChanged(
                                TransactionTypeEnum.entries[page],
                                text
                            )
                        )
                    },
                    transactionType = TransactionTypeEnum.entries[page],
                    hintFormatSeparator = state.hintFormatSeparator,
                    expensesFormat = state.expenseFormat,
                    currency = state.currency,
                    supportingText = when (TransactionTypeEnum.entries[page]) {
                        TransactionTypeEnum.Expenses -> {
                            if (state.expenseAmountSupportText != null)
                                state.expenseAmountSupportText.asString()
                            else null
                        }

                        TransactionTypeEnum.Income -> {
                            if (state.incomeAmountSupportText != null)
                                state.incomeAmountSupportText.asString()
                            else null
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onNext = {
                            noteFocus.requestFocus()
                        }
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                TransactionNoteTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(noteFocus),
                    text = when (TransactionTypeEnum.entries[page]) {
                        TransactionTypeEnum.Expenses -> state.expensesNote
                        TransactionTypeEnum.Income -> state.incomeNote
                    },
                    onTextChanged = { text ->
                        onAction(
                            AddTransactionAction.OnExpenseOrIncomeNoteChanged(
                                TransactionTypeEnum.entries[page],
                                text
                            )
                        )
                    },
                    supportingText = when (TransactionTypeEnum.entries[page]) {
                        TransactionTypeEnum.Expenses -> {
                            if (state.expenseNoteSupportText != null)
                                state.expenseNoteSupportText.asString()
                            else null
                        }

                        TransactionTypeEnum.Income -> {
                            if (state.incomeNoteSupportText != null)
                                state.incomeNoteSupportText.asString()
                            else null
                        }
                    },keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddTransactionScreenPreview() {
    SpendLessTheme {
        AddTransactionScreen(
            state = AddTransactionState(),
            onAction = {}
        )
    }
}