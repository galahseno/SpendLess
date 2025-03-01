package id.dev.spendless.core.domain

import id.dev.spendless.core.domain.model.AddTransactionModel
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result

interface CoreRepository {
    suspend fun createTransaction(
        addTransactionModel: AddTransactionModel
    ): Result<Unit, DataError.Local>

    suspend fun checkSessionPin(
        userId: Int,
        pin: String
    ): Result<Boolean, DataError.Local>
}