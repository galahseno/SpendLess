package id.dev.spendless.core.domain

import id.dev.spendless.core.domain.model.AddTransaction
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result

interface CoreRepository {
    suspend fun createTransaction(
        addTransaction: AddTransaction
    ): Result<Unit, DataError.Local>
}