package id.dev.spendless.transaction.presentation.all_transaction

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class AllTransactionViewModel(

): ViewModel() {
    private val _state = MutableStateFlow(AllTransactionState())
    val state = _state.asStateFlow()

    private val _event = Channel<AllTransactionEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: AllTransactionAction) {

    }
}