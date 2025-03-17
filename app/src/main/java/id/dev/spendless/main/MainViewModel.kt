package id.dev.spendless.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.core.domain.SettingPreferences
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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

        combine(
            settingPreferences.getSessionExpired().distinctUntilChanged(),
            snapshotFlow { state.fromWidget }.distinctUntilChanged()
        ) { isSessionExpired, fromWidget ->
            state = state.copy(
                isSessionExpired = isSessionExpired,
            )
            if (fromWidget && !isSessionExpired) {
                settingPreferences.changeAddBottomSheetValue(true)
                state = state.copy(fromWidget = false)
            }
        }.launchIn(viewModelScope)
    }

    fun captureBackStack(backStack: List<String>) {
        state = state.copy(backStack = emptyList())
        state = state.copy(backStack = backStack)
    }

    fun actionCreateTransaction() {
        state = state.copy(fromWidget = true)
    }

    fun resetSession() {
        viewModelScope.launch {
            if (settingPreferences.getUserId().first() != -1) {
                settingPreferences.changeSession(true)
            }
        }
    }

    fun resetSessionAndCloseBottomSheet() {
        resetSession()
        viewModelScope.launch {
            settingPreferences.changeAddBottomSheetValue(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        resetSession()
    }
}