package id.dev.spendless.dashboard.data

import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.domain.EncryptionService
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.transaction.Transaction
import id.dev.spendless.core.domain.model.transaction.LargestTransaction
import id.dev.spendless.core.domain.model.transaction.Balance
import id.dev.spendless.core.domain.model.transaction.CategoryWithEmoji
import id.dev.spendless.core.domain.model.transaction.DecryptedCategoryWithEmoji
import id.dev.spendless.core.domain.model.transaction.DecryptedLargestTransaction
import id.dev.spendless.core.domain.model.transaction.EncryptedCategoryWithEmoji
import id.dev.spendless.core.domain.model.transaction.EncryptedLargestTransaction
import id.dev.spendless.dashboard.domain.DashboardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.text.toDouble

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val settingPreferences: SettingPreferences,
    private val encryptionService: EncryptionService,
) : DashboardRepository {
    override fun getTotalBalance(): Flow<Double?> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getTotalBalance(userId).map { totalBalances ->
                totalBalances.sumOf { balance ->
                    decryptAmount(balance)
                }
            }
        }
    }

    override fun getLargestTransactionCategoryAllTime(): Flow<CategoryWithEmoji?> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getLargestTransactionCategoryAllTime(userId)
                .map { encryptedCategoryWithEmojis ->
                    val decryptedCategoryWithEmoji =
                        encryptedCategoryWithEmojis.mapNotNull { encryptedCategoryWithEmoji ->
                            encryptedCategoryWithEmoji?.let { decryptTransactionForCategory(it) }
                        }.filter { it.amount < 0 }
                    val groupByName = decryptedCategoryWithEmoji.groupBy { it.categoryName }
                    val categorySums = groupByName.mapValues { (_, category) ->
                        category.sumOf { it.amount }
                    }
                    categorySums.entries.minByOrNull { it.value }?.let { (categoryName, _) ->
                        val firstTransaction =
                            decryptedCategoryWithEmoji.firstOrNull {
                                it.categoryName == categoryName
                            }
                        firstTransaction?.let {
                            CategoryWithEmoji(
                                categoryName = it.categoryName,
                                categoryEmoji = it.categoryEmoji
                            )
                        }
                    }
                }
        }
    }

    override fun getLargestTransaction(): Flow<LargestTransaction?> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getLargestTransaction(userId)
                .map { encryptedLargestTransactions ->
                    val decryptedLargestTransaction =
                        encryptedLargestTransactions.mapNotNull { encryptedLargestTransaction ->
                            encryptedLargestTransaction?.let { decryptLargestTransaction(it) }
                        }
                            .filter { it.amount < 0 }
                            .minByOrNull { it.amount }

                    decryptedLargestTransaction?.let {
                        LargestTransaction(
                            transactionName = it.transactionName,
                            amount = it.amount.toString(),
                            createdAt = it.createdAt.toString()
                        )
                    }
                }
        }
    }

    override fun getTotalSpentPreviousWeek(): Flow<Double?> {
        val now = LocalDateTime.now()
        val startOfCurrentWeek =
            now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).withHour(0).withMinute(0)
                .withSecond(0).withNano(0)
        val startOfPreviousWeek = startOfCurrentWeek.minusWeeks(1)

        val startOfPreviousWeekTimestamp =
            startOfPreviousWeek.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val startOfCurrentWeekTimestamp =
            startOfCurrentWeek.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getTotalSpentPreviousWeek(
                userId,
                startOfPreviousWeekTimestamp,
                startOfCurrentWeekTimestamp
            ).map { totalBalances ->
                totalBalances.sumOf { balance ->
                    decryptAmount(balance)
                }.takeIf { it < 0 }
            }
        }
    }

    override fun getLatestTransactions(): Flow<List<Transaction>> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getLatestTransactions(userId).map { transactions ->
                transactions.map { decryptTransactions(it) }
            }
        }
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getAllTransactions(userId).map { transactions ->
                transactions.map { decryptTransactions(it) }
            }
        }
    }

    private fun decryptAmount(balance: Balance): Double {
        val decryptedAmount =
            encryptionService.decrypt(balance.amount, balance.amountIv)
        return decryptedAmount.toDouble()
    }

    private fun decryptTransactionForCategory(
        encryptedCategoryWithEmoji: EncryptedCategoryWithEmoji
    ): DecryptedCategoryWithEmoji {
        val categoryName = encryptionService.decrypt(
            encryptedCategoryWithEmoji.categoryName,
            encryptedCategoryWithEmoji.categoryNameIv
        )
        val categoryEmoji = encryptionService.decrypt(
            encryptedCategoryWithEmoji.categoryEmoji,
            encryptedCategoryWithEmoji.categoryEmojiIv
        )
        val amount =
            encryptionService.decrypt(
                encryptedCategoryWithEmoji.amount,
                encryptedCategoryWithEmoji.amountIv
            ).toDouble()

        return DecryptedCategoryWithEmoji(
            categoryName = categoryName,
            categoryEmoji = categoryEmoji,
            amount = amount
        )
    }

    private fun decryptLargestTransaction(
        encryptedLargestTransaction: EncryptedLargestTransaction
    ): DecryptedLargestTransaction {
        val transactionName = encryptionService.decrypt(
            encryptedLargestTransaction.transactionName,
            encryptedLargestTransaction.transactionNameIv
        )
        val amount = encryptionService.decrypt(
            encryptedLargestTransaction.amount,
            encryptedLargestTransaction.amountIv
        ).toDouble()

        return DecryptedLargestTransaction(
            transactionName = transactionName,
            amount = amount,
            createdAt = encryptedLargestTransaction.createdAt
        )
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