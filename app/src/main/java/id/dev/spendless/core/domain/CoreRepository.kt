package id.dev.spendless.core.domain

import id.dev.spendless.core.domain.model.TransactionModel
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result

interface CoreRepository {
    suspend fun createTransaction(
        transactionModel: TransactionModel
    ): Result<Unit, DataError.Local>
}