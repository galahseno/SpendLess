package id.dev.spendless.auth.presentation.register

import id.dev.spendless.core.presentation.ui.preferences.DecimalSeparatorEnum
import id.dev.spendless.core.presentation.ui.preferences.ExpensesFormatEnum
import id.dev.spendless.core.presentation.ui.preferences.ThousandsSeparatorEnum

sealed interface RegisterAction {
    data class OnUsernameChanged(val username: String) : RegisterAction
    data object OnRegisterNextClick : RegisterAction
    data object OnLoginClick : RegisterAction

    data object OnBackClick : RegisterAction
    data class OnInputCreatePin(val pin: String) : RegisterAction
    data object OnDeleteCreatePin : RegisterAction

    data object OnResetPin : RegisterAction
    data object OnDeleteRepeatPin : RegisterAction
    data class OnInputRepeatPin(val repeatPin: String) : RegisterAction

    data class OnExpensesFormatSelected(val expense: ExpensesFormatEnum) : RegisterAction
    data class OnDecimalSeparatorSelected(val separator: DecimalSeparatorEnum) : RegisterAction
    data class OnThousandSeparatorSelected(val separator: ThousandsSeparatorEnum) : RegisterAction
}