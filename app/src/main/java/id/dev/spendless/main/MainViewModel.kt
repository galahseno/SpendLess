package id.dev.spendless.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.core.domain.SettingPreferences
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingPreferences: SettingPreferences
) : ViewModel() {
    var state by mutableStateOf(MainState())
        private set

    init {
        state = state.copy(isCheckingAuth = true)
        settingPreferences
            .getUserId()
            .distinctUntilChanged()
            .onEach { userId ->
                state = state.copy(
                    isCheckingAuth = false, isLoggedIn = userId != -1
                )
            }.launchIn(viewModelScope)

        settingPreferences
            .getSessionExpired()
            .distinctUntilChanged()
            .onEach {
                state = state.copy(isSessionExpired = it)
            }.launchIn(viewModelScope)
    }

    fun captureBackStack(backStack: List<String>) {
        state = state.copy(backStack = emptyList())
        state = state.copy(backStack = backStack)
    }

    fun resetSession() {
        viewModelScope.launch {
            settingPreferences.changeSession(true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        resetSession()
    }
}