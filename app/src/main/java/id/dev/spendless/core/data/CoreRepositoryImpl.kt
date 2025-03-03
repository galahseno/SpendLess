package id.dev.spendless.core.data

import id.dev.spendless.core.data.database.dao.PreferencesDao
import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.data.database.dao.UserDao
import id.dev.spendless.core.data.database.entity.PreferencesEntity
import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.EncryptionService
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.transaction.AddTransactionModel
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlin.coroutines.coroutineContext

class CoreRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val userDao: UserDao,
    private val preferencesDao: PreferencesDao,
    private val settingPreferences: SettingPreferences,
    private val encryptionService: EncryptionService
) : CoreRepository {
    override suspend fun createTransaction(addTransactionModel: AddTransactionModel): Result<Unit, DataError.Local> {
        try {
            val userId = settingPreferences.getUserId().first()
            val (transactionNameEncrypted, transactionNameIv) = encryptionService.encrypt(
                addTransactionModel.transactionName
            )
            val (categoryEmojiEncrypted, categoryEmojiIv) = encryptionService.encrypt(
                addTransactionModel.categoryEmoji
            )
            val (categoryNameEncrypted, categoryNameIv) = encryptionService.encrypt(
                addTransactionModel.categoryName
            )
            val (amountEncrypted, amountIv) = encryptionService.encrypt(addTransactionModel.amount.toString())
            val (noteEncrypted, noteIv) = encryptionService.encrypt(addTransactionModel.note)
            val (repeatEncrypted, repeatIv) = encryptionService.encrypt(addTransactionModel.repeat)

            val transactionEntity = TransactionEntity(
                userId = userId,
                transactionName = transactionNameEncrypted,
                categoryEmoji = categoryEmojiEncrypted,
                categoryName = categoryNameEncrypted,
                amount = amountEncrypted,
                note = noteEncrypted,
                createdAt = addTransactionModel.createdAt,
                repeat = repeatEncrypted,
                transactionNameIv = transactionNameIv,
                categoryEmojiIv = categoryEmojiIv,
                categoryNameIv = categoryNameIv,
                amountIv = amountIv,
                noteIv = noteIv,
                repeatIv = repeatIv
            )
            val transactionId =
                transactionDao.createTransaction(transactionEntity)

            if (transactionId == -1L) {
                return Result.Error(DataError.Local.ERROR_PROSES)
            }
            return Result.Success(Unit)

        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    override suspend fun checkSessionPin(
        userId: Int,
        pin: String
    ): Result<Boolean, DataError.Local> {
        try {
            val user =
                userDao.getUserById(userId) ?: return Result.Error(DataError.Local.USER_NOT_EXIST)
            if (encryptionService.decrypt(user.pin, user.iv) == pin) {
                settingPreferences.updateLatestTimeStamp()
                settingPreferences.changeSession(false)
            }

            return Result.Success(encryptionService.decrypt(user.pin, user.iv) == pin)
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    override suspend fun logout(): Result<Unit, DataError.Local> {
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
                return Result.Error(DataError.Local.ERROR_PROSES)
            }
            settingPreferences.logout()

            return Result.Success(Unit)
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return Result.Error(DataError.Local.ERROR_PROSES)
        }
    }
}