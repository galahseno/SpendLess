package id.dev.spendless.core.domain.util

sealed interface DataError: Error {
    enum class Local: DataError {
        ERROR_PROSES,
        USER_EXIST,
        USER_NOT_EXIST,
        USER_AND_PIN_INCORRECT,
    }
}