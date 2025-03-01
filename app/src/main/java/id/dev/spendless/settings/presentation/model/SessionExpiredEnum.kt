package id.dev.spendless.settings.presentation.model

enum class SessionExpiredEnum(val millis: Long) {
    FIVE_MINUTES(300000),
    FIFTEEN_MINUTES(900000),
    THIRTY_MINUTES(1800000),
    ONE_HOUR(3600000);

    companion object {
        fun fromMinutes(millis: Long): SessionExpiredEnum {
            return when (millis) {
                300000L -> FIVE_MINUTES
                900000L -> FIFTEEN_MINUTES
                1800000L -> THIRTY_MINUTES
                3600000L -> ONE_HOUR
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