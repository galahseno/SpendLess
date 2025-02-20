package id.dev.spendless.core.presentation.ui.transaction.repeat_interval

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class DayOfWeekEnum(val dayOfWeek: DayOfWeek) {
    MONDAY(DayOfWeek.MONDAY),
    TUESDAY(DayOfWeek.TUESDAY),
    WEDNESDAY(DayOfWeek.WEDNESDAY),
    THURSDAY(DayOfWeek.THURSDAY),
    FRIDAY(DayOfWeek.FRIDAY),
    SATURDAY(DayOfWeek.SATURDAY),
    SUNDAY(DayOfWeek.SUNDAY);

    companion object {
        fun fromDayOfWeek(dayOfWeek: DayOfWeek): DayOfWeekEnum? {
            return entries.find { it.dayOfWeek == dayOfWeek }
        }
    }
}

// TODO ovveride toString
enum class RepeatIntervalEnum(
    val repeatName: String,
    val formattedDate: String = "",
) {
    DoesNotRepeat("Does not repeat"),
    Daily("Daily"),
    Weekly(
        "Weekly",
        formattedDate = "on ${
            DayOfWeekEnum.fromDayOfWeek(LocalDate.now().dayOfWeek)?.name?.lowercase()
                ?.replaceFirstChar { it.uppercase() } ?: "Monday"
        }"),

    Monthly(
        "Monthly",
        formattedDate = "on the ${LocalDate.now().dayOfMonth} ${getDayOfMonthSuffix(LocalDate.now().dayOfMonth)}"
    ),
    Yearly(
        "Yearly",
        formattedDate = "on the ${
            LocalDate.of(
                LocalDate.now().year,
                LocalDate.now().monthValue,
                LocalDate.now().dayOfMonth
            ).format(
                DateTimeFormatter.ofPattern("MMM dd")
            )
        }"
    )
}

fun getDayOfMonthSuffix(day: Int): String {
    return when (day) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }
}