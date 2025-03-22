package id.dev.spendless.core.presentation.export

import id.dev.spendless.core.domain.model.export.ExportFormatEnum
import id.dev.spendless.core.domain.model.export.ExportRangeEnum
import id.dev.spendless.core.domain.model.export.MonthYear
import id.dev.spendless.core.presentation.ui.UiText
import java.time.LocalDate

data class ExportState(
    val selectedExportRange: ExportRangeEnum = ExportRangeEnum.LAST_THREE_MONTHS,
    val selectedExportFormat: ExportFormatEnum = ExportFormatEnum.CSV,
    val monthYearOptions: List<MonthYear> = emptyList(),
    val selectedMonthYear: MonthYear = MonthYear(
        LocalDate.now().year,
        LocalDate.now().monthValue
    ),
    val isLoading: Boolean = false,
    val isErrorVisible: Boolean = false,
    val errorMessage: UiText? = null,
)
