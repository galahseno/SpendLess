package id.dev.spendless.transaction.presentation.add_transaction

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class AddTransactionViewModel(

): ViewModel() {
    private val _state = MutableStateFlow(AddTransactionState())
    val state = _state.asStateFlow()

    private val _event = Channel<AddTransactionEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: AddTransactionAction) {

    }
}