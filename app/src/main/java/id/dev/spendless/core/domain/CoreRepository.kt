package id.dev.spendless.core.domain

import id.dev.spendless.core.domain.model.export.ExportFormatEnum
import id.dev.spendless.core.domain.model.export.ExportRangeEnum
import id.dev.spendless.core.domain.model.export.MonthYear
import id.dev.spendless.core.domain.model.transaction.AddTransactionModel
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

    suspend fun exportTransactions(
        range: ExportRangeEnum,
        format: ExportFormatEnum,
        specificMonth: MonthYear
    ): Result<String, DataError.Local>

    suspend fun logout(): Result<Unit, DataError.Local>
}