package id.dev.spendless.transaction.data

import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.domain.EncryptionService
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
    private val encryptionService: EncryptionService
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getAllTransactions(userId).map { transactions ->
                transactions.map { decryptTransactions(it) }
            }
        }
    }

    private fun decryptTransactions(transactionEntity: TransactionEntity): Transaction {
        val transactionName = encryptionService.decrypt(
            transactionEntity.transactionName,
            transactionEntity.transactionNameIv
        )
        val categoryEmoji = encryptionService.decrypt(
            transactionEntity.categoryEmoji,
            transactionEntity.categoryEmojiIv
        )
        val categoryName = encryptionService.decrypt(
            transactionEntity.categoryName,
            transactionEntity.categoryNameIv
        )
        val amount = encryptionService.decrypt(
            transactionEntity.amount, transactionEntity.amountIv
        )

        val note = encryptionService.decrypt(transactionEntity.note, transactionEntity.noteIv)
        val repeat = encryptionService.decrypt(transactionEntity.repeat, transactionEntity.repeatIv)

        return Transaction(
            id = transactionEntity.id,
            transactionName = transactionName,
            categoryEmoji = categoryEmoji,
            categoryName = categoryName,
            amount = amount,
            note = note,
            createdAt = transactionEntity.createdAt,
            repeatInterval = repeat
        )
    }
}