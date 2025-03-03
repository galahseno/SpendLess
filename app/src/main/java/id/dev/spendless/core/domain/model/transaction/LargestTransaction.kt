package id.dev.spendless.core.domain.model.transaction

data class EncryptedLargestTransaction(
    val transactionName: String,
    val amount: String,
    val createdAt: Long,
    val transactionNameIv: String,
    val amountIv: String,
)

data class DecryptedLargestTransaction(
    val transactionName: String,
    val amount: Double,
    val createdAt: Long,
)

data class LargestTransaction(
    val transactionName: String? = null,
    val amount: String? = null,
    val createdAt: String? = null,
)
