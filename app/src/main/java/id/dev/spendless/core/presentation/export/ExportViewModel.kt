package id.dev.spendless.core.presentation.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.dev.spendless.R
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.export.ExportFormatEnum
import id.dev.spendless.core.domain.model.export.ExportRangeEnum
import id.dev.spendless.core.domain.model.export.MonthYear
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import id.dev.spendless.core.presentation.ui.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExportViewModel(
    private val settingPreferences: SettingPreferences,
    private val coreRepository: CoreRepository
) : ViewModel() {
    private val _state = MutableStateFlow(ExportState())
    val state = _state.asStateFlow()

    private val _event = Channel<ExportEvent>()
    val event = _event.receiveAsFlow()

    init {
        _state.update {
            it.copy(monthYearOptions = generateMonthYearList())
        }
    }

    fun onAction(action: ExportAction) {
        when (action) {
            is ExportAction.OnSelectedExportRange -> handleOnSelectedExportRange(action.range)
            is ExportAction.OnSelectedExportFormat -> handleOnSelectedExportFormat(action.format)
            is ExportAction.OnSelectedMonth -> handleOnSelectedMonth(action.monthYear)
            is ExportAction.OnExportClick -> handleOnExportClick()
            else -> Unit
        }
    }

    private fun handleOnExportClick() {
        viewModelScope.launch {
            val isSessionExpired = settingPreferences.checkSessionExpired()
            if (isSessionExpired) return@launch

            _state.update { it.copy(isLoading = true) }

            val state = state.value
            val result = coreRepository.exportTransactions(
                range = state.selectedExportRange,
                format = state.selectedExportFormat,
                specificMonth = state.selectedMonthYear
            )

            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _event.send(
                        ExportEvent.ExportSuccess(
                            result.data,
                            _state.value.selectedExportFormat
                        )
                    )
                }

                is Result.Error -> {
                    if (!_state.value.isErrorVisible) {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                isErrorVisible = true,
                                errorMessage = when (result.error) {
                                    DataError.Local.NO_DATA -> UiText.StringResource(R.string.no_data_export)
                                    else -> UiText.StringResource(R.string.error_proses)
                                }
                            )
                        }
                        dismissError()
                    }
                }
            }
        }
    }

    private fun handleOnSelectedMonth(monthYear: MonthYear) {
        _state.update {
            it.copy(selectedMonthYear = monthYear)
        }
    }

    private fun handleOnSelectedExportFormat(format: ExportFormatEnum) {
        _state.update {
            it.copy(selectedExportFormat = format)
        }
    }

    private fun handleOnSelectedExportRange(range: ExportRangeEnum) {
        _state.update {
            it.copy(selectedExportRange = range)
        }
    }

    private fun generateMonthYearList(monthsCount: Int = 12): List<MonthYear> {
        val currentDate = LocalDate.now()
        return (0 until monthsCount).map { i ->
            val date = currentDate.minusMonths(i.toLong())
            MonthYear(date.year, date.monthValue)
        }.sortedByDescending { it.year * 100 + it.month }
    }

    private fun dismissError() {
        viewModelScope.launch {
            delay(2000)
            _state.update { it.copy(isErrorVisible = false) }
        }
    }
}