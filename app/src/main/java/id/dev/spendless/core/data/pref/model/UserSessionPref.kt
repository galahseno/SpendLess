package id.dev.spendless.core.data.pref.model

import id.dev.spendless.core.domain.model.UserSession
import kotlinx.serialization.Serializable

@Serializable
data class UserSessionPref(
    val userId: Int = -1,
    val username: String = "",
    val expensesFormat: String = "MinusPrefix",
    val currencySymbol: String = "USD",
    val decimalSeparator: String = "Dot",
    val thousandSeparator: String = "Comma",
    val addBottomSheetState: Boolean = false,
)

fun UserSessionPref.toUserSession(): UserSession {
    return UserSession(
        userId = userId,
        username = username,
        expensesFormat = expensesFormat,
        currencySymbol = currencySymbol,
        decimalSeparator = decimalSeparator,
        thousandSeparator = thousandSeparator,
        addBottomSheetState = addBottomSheetState
    )
}


