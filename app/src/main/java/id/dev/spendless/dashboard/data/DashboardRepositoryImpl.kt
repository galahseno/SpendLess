package id.dev.spendless.dashboard.data

import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.data.utils.CryptoHelper
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.transaction.CategoryWithEmoji
import id.dev.spendless.core.domain.model.transaction.LargestTransaction
import id.dev.spendless.core.domain.model.transaction.Transaction
import id.dev.spendless.dashboard.domain.DashboardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val settingPreferences: SettingPreferences,
    private val cryptoHelper: CryptoHelper,
) : DashboardRepository {
    override fun getTotalBalance(): Flow<Double?> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getTotalBalance(userId).map { totalBalances ->
                totalBalances.sumOf { balance ->
                    cryptoHelper.decryptAmount(balance)
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
                            encryptedCategoryWithEmoji?.let { cryptoHelper.decryptCategoryWithEmoji(it) }
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
                            encryptedLargestTransaction?.let { cryptoHelper.decryptLargestTransaction(it) }
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
                    cryptoHelper.decryptAmount(balance)
                }.takeIf { it < 0 }
            }
        }
    }

    override fun getLatestTransactions(): Flow<List<Transaction>> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getLatestTransactions(userId).map { transactions ->
                transactions.map { cryptoHelper.decryptTransaction(it) }
            }
        }
    }
}