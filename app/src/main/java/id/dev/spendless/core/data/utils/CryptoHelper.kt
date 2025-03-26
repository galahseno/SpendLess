package id.dev.spendless.core.data.utils

import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.domain.EncryptionService
import id.dev.spendless.core.domain.model.transaction.AddTransactionModel
import id.dev.spendless.core.domain.model.transaction.Balance
import id.dev.spendless.core.domain.model.transaction.DecryptedCategoryWithEmoji
import id.dev.spendless.core.domain.model.transaction.DecryptedLargestTransaction
import id.dev.spendless.core.domain.model.transaction.EncryptedCategoryWithEmoji
import id.dev.spendless.core.domain.model.transaction.EncryptedLargestTransaction
import id.dev.spendless.core.domain.model.transaction.Transaction

class CryptoHelper(
    private val encryptionService: EncryptionService
) {
    fun encryptField(value: String): Pair<String, String> {
        return encryptionService.encrypt(value)
    }

    fun encryptTransaction(userId: Int, input: AddTransactionModel): TransactionEntity {
        val (transactionNameEncrypted, transactionNameIv) = encryptionService
            .encrypt(input.transactionName)

        val (categoryEmojiEncrypted, categoryEmojiIv) = encryptionService
            .encrypt(input.categoryEmoji)

        val (categoryNameEncrypted, categoryNameIv) = encryptionService
            .encrypt(input.categoryName)

        val (amountEncrypted, amountIv) = encryptionService.encrypt(input.amount.toString())
        val (noteEncrypted, noteIv) = encryptionService.encrypt(input.note)
        val (repeatEncrypted, repeatIv) = encryptionService.encrypt(input.repeat)


        return TransactionEntity(
            userId = userId,
            transactionName = transactionNameEncrypted,
            categoryEmoji = categoryEmojiEncrypted,
            categoryName = categoryNameEncrypted,
            amount = amountEncrypted,
            note = noteEncrypted,
            createdAt = input.createdAt,
            repeat = repeatEncrypted,
            transactionNameIv = transactionNameIv,
            categoryEmojiIv = categoryEmojiIv,
            categoryNameIv = categoryNameIv,
            amountIv = amountIv,
            noteIv = noteIv,
            repeatIv = repeatIv
        )
    }

    fun decryptTransaction(transactionEntity: TransactionEntity): Transaction {
        return Transaction(
            id = transactionEntity.id,
            transactionName = decryptField(
                transactionEntity.transactionName,
                transactionEntity.transactionNameIv
            ),
            categoryEmoji = decryptField(
                transactionEntity.categoryEmoji,
                transactionEntity.categoryEmojiIv
            ),
            categoryName = decryptField(
                transactionEntity.categoryName,
                transactionEntity.categoryNameIv
            ),
            amount = decryptField(transactionEntity.amount, transactionEntity.amountIv),
            note = decryptField(transactionEntity.note, transactionEntity.noteIv),
            createdAt = transactionEntity.createdAt,
            repeatInterval = decryptField(transactionEntity.repeat, transactionEntity.repeatIv)
        )
    }

    fun decryptCategoryWithEmoji(
        encrypted: EncryptedCategoryWithEmoji
    ): DecryptedCategoryWithEmoji {
        return DecryptedCategoryWithEmoji(
            categoryName = decryptField(encrypted.categoryName, encrypted.categoryNameIv),
            categoryEmoji = decryptField(encrypted.categoryEmoji, encrypted.categoryEmojiIv),
            amount = decryptField(encrypted.amount, encrypted.amountIv).toDouble()
        )
    }


    fun decryptLargestTransaction(
        encrypted: EncryptedLargestTransaction
    ): DecryptedLargestTransaction {
        return DecryptedLargestTransaction(
            transactionName = decryptField(encrypted.transactionName, encrypted.transactionNameIv),
            amount = decryptField(encrypted.amount, encrypted.amountIv).toDouble(),
            createdAt = encrypted.createdAt
        )
    }

    fun decryptAmount(balance: Balance): Double {
        return decryptField(balance.amount, balance.amountIv).toDouble()
    }

    fun decryptField(encryptedValue: String, iv: String): String {
        return encryptionService.decrypt(encryptedValue, iv)
    }
}