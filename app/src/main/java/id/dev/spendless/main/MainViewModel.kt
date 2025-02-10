package id.dev.spendless.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.core.domain.SettingPreferences
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainViewModel(
    settingPreferences: SettingPreferences
) : ViewModel() {
    var state by mutableStateOf(MainState())
        private set

    init {
        state = state.copy(isCheckingAuth = true)
        settingPreferences
            .getUserStatus()
            .onEach { userId ->
                state = state.copy(
                    isCheckingAuth = false, isLoggedIn = userId != -1
                )
            }.launchIn(viewModelScope)
    }
}