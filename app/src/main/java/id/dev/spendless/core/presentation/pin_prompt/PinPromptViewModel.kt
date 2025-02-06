package id.dev.spendless.core.presentation.pin_prompt

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class PinPromptViewModel(

): ViewModel() {
    private val _state = MutableStateFlow(PinPromptState())
    val state = _state.asStateFlow()

    private val _event = Channel<PinPromptEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: PinPromptAction) {

    }
}