package id.dev.spendless.auth.data

import id.dev.spendless.auth.domain.AuthRepository
import id.dev.spendless.core.data.database.dao.UserDao
import id.dev.spendless.core.data.database.entity.UserEntity
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.joinAll
import kotlin.coroutines.coroutineContext

class AuthRepositoryImpl(
    private val settingPreferences: SettingPreferences,
    private val userDao: UserDao,
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
            val userEntity = UserEntity(username = username, pin = pin)
            val userId = userDao.createUser(userEntity)

            if (userId == -1L) {
                return Result.Error(DataError.Local.ERROR_PROSES)
            }

            settingPreferences.saveRegisterSession(
                userId = userId.toInt(),
                username = username,
                expensesFormat = expensesFormat,
                currencySymbol = currencySymbol,
                decimalSeparator = decimalSeparator,
                thousandSeparator = thousandSeparator
            )
            joinAll()

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
            val isUserExist = userDao.isUserExist(username)

            if (!isUserExist) {
                return Result.Error(DataError.Local.USER_NOT_EXIST)
            }

            val userEntity = userDao.loginAccount(username, pin)
                ?: return Result.Error(DataError.Local.USER_AND_PIN_INCORRECT)

            settingPreferences.saveLoginSession(
                userId = userEntity.id,
                username = userEntity.username,
            )
            joinAll()

            return Result.Success(Unit)

        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

}