package id.dev.spendless.dashboard.presentation.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.formatTimestamp(): String {
    val instant = Instant.ofEpochMilli(this)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val today = LocalDate.now()
    val date = dateTime.toLocalDate()

    return when {
        date.isEqual(today) -> "Today"
        date.isEqual(today.minusDays(1)) -> "Yesterday"
        else -> dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
    }
}