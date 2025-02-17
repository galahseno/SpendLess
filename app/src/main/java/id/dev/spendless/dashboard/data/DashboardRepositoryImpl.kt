package id.dev.spendless.dashboard.data

import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.CategoryWithEmoji
import id.dev.spendless.core.domain.model.LargestTransaction
import id.dev.spendless.dashboard.domain.DashboardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val settingPreferences: SettingPreferences,
) : DashboardRepository {
    override fun getTotalBalance(): Flow<Double?> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getTotalBalance(userId)
        }
    }

    override fun getLargestTransactionCategoryAllTime(): Flow<CategoryWithEmoji?> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getLargestTransactionCategoryAllTime(userId)
        }
    }

    override fun getLargestTransaction(): Flow<LargestTransaction?> {
        return settingPreferences.getUserId().flatMapConcat { userId ->
            transactionDao.getLargestTransaction(userId)
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
            )
        }
    }

}