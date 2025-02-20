package id.dev.spendless.settings.presentation.model

enum class SessionExpiredEnum(val minutes: Int) {
    FIVE_MINUTES(5),
    FIFTEEN_MINUTES(15),
    THIRTY_MINUTES(30),
    ONE_HOUR(60);

    companion object {
        fun fromMinutes(minutes: Int): SessionExpiredEnum {
            return when (minutes) {
                5 -> FIVE_MINUTES
                15 -> FIFTEEN_MINUTES
                30 -> THIRTY_MINUTES
                60 -> ONE_HOUR
                else -> FIVE_MINUTES
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            FIVE_MINUTES -> "5 min"
            FIFTEEN_MINUTES -> "15 min"
            THIRTY_MINUTES -> "30 min"
            ONE_HOUR -> "1 hour"
        }
    }
}