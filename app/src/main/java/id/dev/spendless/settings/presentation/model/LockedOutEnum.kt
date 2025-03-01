package id.dev.spendless.settings.presentation.model

// TODO impl
enum class LockedOutEnum(val millis: Long) {
    FIFTEEN_SECONDS(15000),
    THIRTY_SECONDS(30000),
    ONE_MINUTE(60000),
    FIVE_MINUTES(300000);

    companion object {
        fun fromSeconds(millis: Long): LockedOutEnum {
            return when (millis) {
                15000L -> FIFTEEN_SECONDS
                30000L -> THIRTY_SECONDS
                60000L -> ONE_MINUTE
                300000L -> FIVE_MINUTES
                else -> FIFTEEN_SECONDS
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            FIFTEEN_SECONDS -> "15s"
            THIRTY_SECONDS -> "30s"
            ONE_MINUTE -> "1 min"
            FIVE_MINUTES -> "5 min"
        }
    }
}