package id.dev.spendless.core.data.util

import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.domain.model.TransactionModel

fun TransactionModel.toTransactionEntity(userId: Int): TransactionEntity {
    return TransactionEntity(
        userId = userId,
        transactionName = transactionName,
        categoryEmoji = categoryEmoji,
        categoryName = categoryName,
        amount = amount,
        note = note,
        createdAt = createdAt,
        repeat = repeat
    )
}