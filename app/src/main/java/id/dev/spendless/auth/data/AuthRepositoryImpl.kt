package id.dev.spendless.auth.data

import id.dev.spendless.auth.domain.AuthRepository
import id.dev.spendless.core.data.database.dao.UserDao
import id.dev.spendless.core.data.database.entity.UserEntity
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class AuthRepositoryImpl(
    private val settingPreferences: SettingPreferences,
    private val userDao: UserDao,
    private val applicationScope: CoroutineScope,
) : AuthRepository {
    override suspend fun checkUsernameExists(username: String): Result<Unit, DataError.Local> {
        try {
            val isUserExist = userDao.isUserExist(username)

            return when {
                isUserExist -> Result.Error(DataError.Local.USER_EXIST)
                else -> Result.Success(Unit)
            }
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    override suspend fun registerAccount(
        username: String,
        pin: String,
        expensesFormat: String,
        currencySymbol: String,
        decimalSeparator: String,
        thousandSeparator: String
    ): Result<Unit, DataError.Local> {
        try {
            val userId = applicationScope.async {
                val userEntity = UserEntity(username = username, pin = pin)
                userDao.createUser(userEntity)
            }.await()

            if (userId == -1L) {
                return Result.Error(DataError.Local.ERROR_PROSES)
            }

            applicationScope.launch {
                settingPreferences.saveRegisterSession(
                    userId = userId.toInt(),
                    username = username,
                    expensesFormat = expensesFormat,
                    currencySymbol = currencySymbol,
                    decimalSeparator = decimalSeparator,
                    thousandSeparator = thousandSeparator
                )
            }.join()

            return Result.Success(Unit)

        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return Result.Error(DataError.Local.ERROR_PROSES)
        }

    }

    override suspend fun loginAccount(
        username: String,
        pin: String
    ): Result<Unit, DataError.Local> {
        try {
            val isUserExist = applicationScope.async {
                userDao.isUserExist(username)
            }.await()

            if (!isUserExist) {
                return Result.Error(DataError.Local.USER_NOT_EXIST)
            }

            val userEntity = applicationScope.async {
                userDao.loginAccount(username, pin)
            }.await()

            if (userEntity == null) {
                return Result.Error(DataError.Local.USER_AND_PIN_INCORRECT)
            }

            applicationScope.launch {
                settingPreferences.saveLoginSession(
                    userId = userEntity.userId,
                    username = userEntity.username,
                )
            }.join()

            return Result.Success(Unit)

        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

}