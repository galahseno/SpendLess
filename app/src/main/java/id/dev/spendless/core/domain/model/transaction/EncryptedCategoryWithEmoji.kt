package id.dev.spendless.core.domain.model.transaction

data class EncryptedCategoryWithEmoji(
    val categoryName: String,
    val categoryEmoji: String,
    val amount: String,
    val categoryNameIv: String,
    val categoryEmojiIv: String,
    val amountIv: String
)

data class DecryptedCategoryWithEmoji(
    val categoryName: String,
    val categoryEmoji: String,
    val amount: Double
)

data class CategoryWithEmoji(
    val categoryName: String? = null,
    val categoryEmoji: String? = null,
)
