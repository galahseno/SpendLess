package id.dev.spendless.core.data

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import id.dev.spendless.core.data.database.dao.PreferencesDao
import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.data.database.dao.UserDao
import id.dev.spendless.core.data.database.entity.PreferencesEntity
import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.data.utils.CryptoHelper
import id.dev.spendless.core.data.utils.ExportHelper
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.export.ExportFormatEnum
import id.dev.spendless.core.domain.model.export.ExportRangeEnum
import id.dev.spendless.core.domain.model.export.MonthYear
import id.dev.spendless.core.domain.model.transaction.AddTransactionModel
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class CoreRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val userDao: UserDao,
    private val preferencesDao: PreferencesDao,
    private val settingPreferences: SettingPreferences,
    private val cryptoHelper: CryptoHelper,
    private val exportHelper: ExportHelper,
    private val context: Context
) : CoreRepository {
    override suspend fun createTransaction(addTransactionModel: AddTransactionModel): Result<Unit, DataError.Local> =
        withContext(Dispatchers.IO) {
            try {
                val userId = settingPreferences.getUserId().first()
                val transactionEntity = cryptoHelper
                    .encryptTransaction(userId = userId, input = addTransactionModel)
                    .copy(userId = userId)

                val transactionId =
                    transactionDao.createTransaction(transactionEntity)

                if (transactionId == -1L) {
                    return@withContext Result.Error(DataError.Local.ERROR_PROSES)
                }
                return@withContext Result.Success(Unit)
            } catch (_: Exception) {
                coroutineContext.ensureActive()
                return@withContext Result.Error(DataError.Local.ERROR_PROSES)
            }
        }

    override suspend fun checkSessionPin(
        userId: Int,
        pin: String
    ): Result<Boolean, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val user =
                userDao.getUserById(userId)
                    ?: return@withContext Result.Error(DataError.Local.USER_NOT_EXIST)
            if (cryptoHelper.decryptField(user.pin, user.iv) == pin) {
                settingPreferences.updateLatestTimeStamp()
                settingPreferences.changeSession(false)
            }

            return@withContext Result.Success(cryptoHelper.decryptField(user.pin, user.iv) == pin)
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return@withContext Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    override suspend fun exportTransactions(
        range: ExportRangeEnum,
        format: ExportFormatEnum,
        specificMonth: MonthYear
    ): Result<String, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val userId = settingPreferences.getUserId().first()
            val transactions = getTransactionsByRange(userId, range, specificMonth)

            if (transactions.isEmpty()) return@withContext Result.Error(DataError.Local.NO_DATA)

            val decryptedTransactions = transactions.map { cryptoHelper.decryptTransaction(it) }
            val fileName = "transactions_${System.currentTimeMillis()}.${format.name.lowercase()}"
            val downloadsDir = exportHelper.getDownloadsDirectory()
            val file = File(downloadsDir, fileName)

            when (format) {
                ExportFormatEnum.CSV -> exportHelper.createCsvFile(file, decryptedTransactions)
                ExportFormatEnum.PDF -> exportHelper.createPdfFile(file, decryptedTransactions)
            }

            val mimeType = when (format) {
                ExportFormatEnum.CSV -> "text/csv"
                ExportFormatEnum.PDF -> "application/pdf"
            }

            val uri = exportHelper.saveFileWithMediaStore(file, mimeType)
            file.delete()
            scanMediaFileIfNeeded(file)

            Result.Success(uri.toString())
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    override suspend fun logout(): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val userid = settingPreferences.getUserId().first()
            val userSession = settingPreferences.getUserSession().first()
            val userSecurity = settingPreferences.getUserSecurity().first()

            val preferencesEntity = PreferencesEntity(
                userId = userid,
                expensesFormat = userSession.expensesFormat,
                currencySymbol = userSession.currencySymbol,
                decimalSeparator = userSession.decimalSeparator,
                thousandSeparator = userSession.thousandSeparator,
                biometricPromptEnable = userSecurity.biometricPromptEnable,
                sessionExpiryDuration = userSecurity.sessionExpiryDuration,
                lockedOutDuration = userSecurity.lockedOutDuration,
            )

            val result = preferencesDao.insertPreferences(preferencesEntity)

            if (result == -1L) {
                return@withContext Result.Error(DataError.Local.ERROR_PROSES)
            }
            settingPreferences.logout()

            return@withContext Result.Success(Unit)
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return@withContext Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    private suspend fun getTransactionsByRange(
        userId: Int,
        range: ExportRangeEnum,
        specificMonth: MonthYear
    ): List<TransactionEntity> {
        return when (range) {
            ExportRangeEnum.ALL_DATA -> transactionDao.getAllTransactions(userId).first()
            ExportRangeEnum.LAST_THREE_MONTHS -> {
                val threeMonthsAgo = LocalDate.now().minusMonths(3)
                transactionDao.getTransactionsAfter(
                    userId,
                    threeMonthsAgo.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
                )
            }

            ExportRangeEnum.LAST_MONTH -> {
                val lastMonth = LocalDate.now().minusMonths(1)
                transactionDao.getTransactionsByMonth(
                    userId,
                    lastMonth.year.toString(),
                    "%02d".format(lastMonth.monthValue)
                )
            }

            ExportRangeEnum.CURRENT_MONTH -> {
                val now = LocalDate.now()
                val firstOfMonth = now.withDayOfMonth(1)

                transactionDao.getTransactionsBetweenDates(
                    userId = userId,
                    startDate = firstOfMonth.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
                    endDate = now.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toEpochSecond()
                )
            }

            ExportRangeEnum.SPECIFIC_MONTH -> {
                transactionDao.getTransactionsByMonth(
                    userId,
                    specificMonth.year.toString(),
                    "%02d".format(specificMonth.month)
                )
            }
        }
    }

    private fun scanMediaFileIfNeeded(file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                null,
                null
            )
        }
    }
}