package id.dev.spendless.auth.data

import id.dev.spendless.auth.domain.AuthRepository
import id.dev.spendless.core.data.database.dao.PreferencesDao
import id.dev.spendless.core.data.database.dao.UserDao
import id.dev.spendless.core.data.database.entity.UserEntity
import id.dev.spendless.core.data.utils.CryptoHelper
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val settingPreferences: SettingPreferences,
    private val userDao: UserDao,
    private val preferencesDao: PreferencesDao,
    private val cryptoHelper: CryptoHelper
) : AuthRepository {
    override suspend fun checkUsernameExists(username: String): Result<Unit, DataError.Local> =
        withContext(Dispatchers.IO) {
            try {
                val isUserExist = userDao.isUserExist(username)

                return@withContext when {
                    isUserExist -> Result.Error(DataError.Local.USER_EXIST)
                    else -> Result.Success(Unit)
                }
            } catch (_: Exception) {
                coroutineContext.ensureActive()
                return@withContext Result.Error(DataError.Local.ERROR_PROSES)
            }
        }

    override suspend fun registerAccount(
        username: String,
        pin: String,
        expensesFormat: String,
        currencySymbol: String,
        decimalSeparator: String,
        thousandSeparator: String
    ): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val (encryptedPin, encryptedIv) = cryptoHelper.encryptField(pin)

            val userEntity = UserEntity(username = username, pin = encryptedPin, iv = encryptedIv)
            val userId = userDao.createUser(userEntity)

            if (userId == -1L) {
                return@withContext Result.Error(DataError.Local.ERROR_PROSES)
            }

            settingPreferences.saveRegisterSession(
                userId = userId.toInt(),
                username = username,
                expensesFormat = expensesFormat,
                currencySymbol = currencySymbol,
                decimalSeparator = decimalSeparator,
                thousandSeparator = thousandSeparator
            )

            return@withContext Result.Success(Unit)

        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return@withContext Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    override suspend fun loginAccount(
        username: String,
        pin: String
    ): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val isUserExist = userDao.isUserExist(username)

            if (!isUserExist) {
                return@withContext Result.Error(DataError.Local.USER_NOT_EXIST)
            }

            val user = userDao.loginAccount(username)
                ?: return@withContext Result.Error(DataError.Local.USER_AND_PIN_INCORRECT)

            if (cryptoHelper.decryptField(user.pin, user.iv) != pin) {
                return@withContext Result.Error(DataError.Local.USER_AND_PIN_INCORRECT)
            }

            preferencesDao.getPreferences(user.id)?.let {
                settingPreferences.updateUserSecurity(
                    biometricPromptEnable = it.biometricPromptEnable,
                    sessionExpiryDuration = it.sessionExpiryDuration,
                    lockedOutDuration = it.lockedOutDuration
                )

                settingPreferences.updateUserSession(
                    expensesFormat = it.expensesFormat,
                    currencySymbol = it.currencySymbol,
                    decimalSeparator = it.decimalSeparator,
                    thousandSeparator = it.thousandSeparator
                )
            }

            settingPreferences.saveLoginSession(
                userId = user.id,
                username = user.username,
            )

            return@withContext Result.Success(Unit)
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return@withContext Result.Error(DataError.Local.ERROR_PROSES)
        }
    }
}