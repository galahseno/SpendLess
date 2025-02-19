package id.dev.spendless.core.domain.model

data class TransactionGroup(
    val formattedDate: String,
    val transactions: List<Transaction>
)

data class Transaction(
    val id: Int,
    val transactionName: String,
    val categoryEmoji: String,
    val categoryName: String,
    val amount: String,
    val note: String = "",
    val createdAt: Long,
    val isNoteOpen: Boolean = false,
)
