package id.dev.spendless.core.domain.model.export

enum class ExportRangeEnum(val displayName: String) {
    ALL_DATA("All Data"),
    LAST_THREE_MONTHS("Last three months"),
    LAST_MONTH("Last month"),
    CURRENT_MONTH("Current month"),
    SPECIFIC_MONTH("Specific Month")
}