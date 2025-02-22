package id.dev.spendless.core.data

import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.model.AddTransactionModel
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import id.dev.spendless.core.data.util.toTransactionEntity
import id.dev.spendless.core.domain.SettingPreferences
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlin.coroutines.coroutineContext

class CoreRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val settingPreferences: SettingPreferences
) : CoreRepository {
    // TODO Encrypt Transaction
    override suspend fun createTransaction(addTransactionModel: AddTransactionModel): Result<Unit, DataError.Local> {
        try {
            val userId = settingPreferences.getUserId().first()
            val transactionId =
                transactionDao.createTransaction(addTransactionModel.toTransactionEntity(userId))

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