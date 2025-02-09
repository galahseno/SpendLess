package id.dev.spendless.auth.domain

import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result

interface AuthRepository {
    suspend fun checkUsernameExists(username: String): Result<Unit, DataError.Local>
    suspend fun registerAccount(
        username:String,
        pin:String,
        expensesFormat: String,
        currencySymbol: String,
        decimalSeparator: String,
        thousandSeparator: String
    ): Result<Unit, DataError.Local>

    suspend fun loginAccount(
        username: String,
        pin: String
    ): Result<Unit, DataError.Local>
}