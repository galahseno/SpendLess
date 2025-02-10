package id.dev.spendless.core.domain.model

data class UserSession(
    val username: String,
    val expensesFormat: String,
    val currencySymbol: String,
    val decimalSeparator: String,
    val thousandSeparator: String,
)
