package id.dev.spendless.core.data

import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.model.AddTransaction
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import id.dev.spendless.core.data.util.toTransactionEntity
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

class CoreRepositoryImpl(
    private val transactionDao: TransactionDao
): CoreRepository {
    override suspend fun createTransaction(addTransaction: AddTransaction): Result<Unit, DataError.Local> {
        try {
            val transactionId =
                transactionDao.createTransaction(addTransaction.toTransactionEntity())

            if (transactionId == -1L) {
                return Result.Error(DataError.Local.ERROR_PROSES)
            }
            return Result.Success(Unit)

        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

}