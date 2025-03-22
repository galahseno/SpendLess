package id.dev.spendless.transaction.domain

import id.dev.spendless.core.domain.model.transaction.Transaction
import kotlinx.coroutines.flow.Flow


interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
}