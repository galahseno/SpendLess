package id.dev.spendless.settings.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class SettingsViewModel(

): ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _event = Channel<SettingsEvent>()
    val event = _event.receiveAsFlow()

    fun onAction(action: SettingsAction) {

    }
}