package id.dev.spendless.settings.presentation.model

enum class LockedOutEnum(val seconds: Int) {
    FIFTEEN_SECONDS(15),
    THIRTY_SECONDS(30),
    ONE_MINUTE(60),
    FIVE_MINUTES(300);

    companion object {
        fun fromSeconds(seconds: Int): LockedOutEnum {
            return when (seconds) {
                15 -> FIFTEEN_SECONDS
                30 -> THIRTY_SECONDS
                60 -> ONE_MINUTE
                300 -> FIVE_MINUTES
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