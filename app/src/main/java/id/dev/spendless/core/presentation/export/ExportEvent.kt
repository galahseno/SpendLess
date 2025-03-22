package id.dev.spendless.core.presentation.export

import id.dev.spendless.core.domain.model.export.ExportFormatEnum

sealed interface ExportEvent {
    data class ExportSuccess(val data: String, val formatEnum: ExportFormatEnum) : ExportEvent
}