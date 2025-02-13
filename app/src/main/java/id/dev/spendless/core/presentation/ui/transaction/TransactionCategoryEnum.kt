package id.dev.spendless.core.presentation.ui.transaction

enum class TransactionCategoryEnum(
    val transactionType: TransactionTypeEnum,
    val categoryEmoji: String,
    val categoryName: String
) {
    Income(TransactionTypeEnum.Income, "\uD83D\uDCB0", "Income"),

    Home(TransactionTypeEnum.Expenses, "\uD83C\uDFE0", "Home"),
    FoodGroceries(TransactionTypeEnum.Expenses, "\uD83C\uDF55", "Food & Groceries"),
    Entertainment(TransactionTypeEnum.Expenses, "\uD83D\uDCBB", "Entertainment"),
    ClothingAccessories(TransactionTypeEnum.Expenses, "\uD83D\uDC54", "Clothing & Accessories"),
    HealthWellness(TransactionTypeEnum.Expenses, "❤\uFE0F", "Health & Wellness"),
    PersonalCare(TransactionTypeEnum.Expenses, "\uD83D\uDEC1", "Personal Care"),
    Transportation(TransactionTypeEnum.Expenses, "\uD83D\uDE97", "Transportation"),
    Education(TransactionTypeEnum.Expenses, "\uD83C\uDF93", "Education"),
    SavingInvestments(TransactionTypeEnum.Expenses, "\uD83D\uDC8E", "Saving & Investments"),
    Other(TransactionTypeEnum.Expenses, "⚙\uFE0F", "Other")
}