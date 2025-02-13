package id.dev.spendless.core.domain.model

data class AddTransaction(
    val userId: Int,
    val transactionName: String,
    val categoryEmoji: String,
    val categoryName: String,
    val amount: Double,
    val note: String = "",
    val createdAt: Long,
    val repeat: String
)
