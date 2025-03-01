package id.dev.spendless.main

data class MainState(
    val isLoggedIn: Boolean? = null,
    val isCheckingAuth: Boolean = false,
    val isSessionExpired: Boolean = false,
    val backStack: List<String> = emptyList()
)
