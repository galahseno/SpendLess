package id.dev.spendless.core.domain

sealed interface DataError: Error {
    enum class Local: DataError {
        ERROR_PROSES,
        USER_EXIST,
        USER_AND_PIN_INVALID,
    }
}