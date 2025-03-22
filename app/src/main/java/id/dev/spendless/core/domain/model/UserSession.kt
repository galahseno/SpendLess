package id.dev.spendless.core.domain.model

data class UserSession(
    val userId: Int,
    val username: String,
    val expensesFormat: String,
    val currencySymbol: String,
    val decimalSeparator: String,
    val thousandSeparator: String,
    val addBottomSheetState: Boolean,
    val exportBottomSheetState: Boolean,
)
