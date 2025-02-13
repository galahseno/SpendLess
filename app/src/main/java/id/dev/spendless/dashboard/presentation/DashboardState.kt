package id.dev.spendless.dashboard.presentation

data class DashboardState(
    val username: String = "",
    val balance: String = "-10382.45",
    val previousWeekSpend: String = "0",
    val transaction: Boolean = true,
    //
)
