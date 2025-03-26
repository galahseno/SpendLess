package id.dev.spendless.transaction.data

import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.data.utils.CryptoHelper
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.transaction.Transaction
import id.dev.spendless.transaction.domain.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionRepositoryImpl(
    private val settingPreferences: SettingPreferences,
    private val transactionDao: TransactionDao,
    private val cryptoHelper: CryptoHelper,
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getAllTransactions(userId).map { transactions ->
                transactions.map { cryptoHelper.decryptTransaction(it) }
            }
        }
    }
}