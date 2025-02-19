package id.dev.spendless.core.data.util

import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.domain.model.AddTransactionModel
import id.dev.spendless.core.domain.model.Transaction

fun AddTransactionModel.toTransactionEntity(userId: Int): TransactionEntity {
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

fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = id,
        transactionName = transactionName,
        categoryEmoji = categoryEmoji,
        categoryName = categoryName,
        amount = amount.toString(),
        note = note,
        createdAt = createdAt,
    )
}