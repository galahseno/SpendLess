package id.dev.spendless.core.presentation.ui.transaction.repeat_interval

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

enum class RepeatIntervalEnum {
    DoesNotRepeat,
    Daily,
    Weekly,
    Monthly,
    Yearly;

    override fun toString(): String {
        val now = LocalDate.now()
        return when (this) {
            DoesNotRepeat -> "Does not repeat"
            Daily -> "Daily"
            Weekly -> "Weekly on ${
                now.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            }"

            Monthly -> "Monthly on the ${
                getValidDayOfMonth(
                    now.dayOfMonth,
                    now.monthValue
                )
            }${getDayOfMonthSuffix(getValidDayOfMonth(now.dayOfMonth, now.monthValue))}"

            Yearly -> "Yearly on ${now.format(DateTimeFormatter.ofPattern("MMM"))} ${
                getValidDayOfMonth(
                    now.dayOfMonth,
                    now.monthValue
                )
            }${getDayOfMonthSuffix(getValidDayOfMonth(now.dayOfMonth, now.monthValue))}"
        }
    }

    private fun getValidDayOfMonth(day: Int, month: Int): Int {
        val lastDayOfMonth = LocalDate.of(LocalDate.now().year, month, 1).lengthOfMonth()
        return if (day > lastDayOfMonth) lastDayOfMonth else day
    }

    private fun getDayOfMonthSuffix(day: Int): String {
        return when (day) {
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
    }
}