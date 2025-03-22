package id.dev.spendless.core.presentation.export

import id.dev.spendless.core.domain.model.export.ExportFormatEnum
import id.dev.spendless.core.domain.model.export.ExportRangeEnum
import id.dev.spendless.core.domain.model.export.MonthYear

sealed interface ExportAction {
    data object OnCloseModalSheet : ExportAction
    data class OnSelectedExportRange(val range: ExportRangeEnum) : ExportAction
    data class OnSelectedExportFormat(val format: ExportFormatEnum) : ExportAction
    data class OnSelectedMonth(val monthYear: MonthYear) : ExportAction
    data object OnExportClick : ExportAction
}