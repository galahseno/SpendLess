package id.dev.spendless.core.data.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val dateFormat = SimpleDateFormat(
        "dd-MM-yyyy",
        Locale.ROOT
    )

    fun format(date: LocalDate): String {
        return dateFormat.format(
            Date.from(date.atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()))
    }
}