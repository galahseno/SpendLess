package id.dev.spendless.auth.domain

import id.dev.spendless.core.domain.DataError
import id.dev.spendless.core.domain.Result

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
}