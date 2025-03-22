package id.dev.spendless.core.presentation.export

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.waterfall
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.dev.spendless.R
import id.dev.spendless.core.domain.model.export.ExportFormatEnum
import id.dev.spendless.core.presentation.design_system.SpendLessTheme
import id.dev.spendless.core.presentation.design_system.component.SpendLessButton
import id.dev.spendless.core.presentation.design_system.component.SpendLessErrorContainer
import id.dev.spendless.core.presentation.design_system.errorHeightClosedKeyboard
import id.dev.spendless.core.presentation.design_system.errorHeightOpenKeyboard
import id.dev.spendless.core.presentation.design_system.screenBackground
import id.dev.spendless.core.presentation.design_system.sheetBackground
import id.dev.spendless.core.presentation.export.component.ExportAppBar
import id.dev.spendless.core.presentation.export.component.ExportFormatDropDown
import id.dev.spendless.core.presentation.export.component.ExportRangeDropDown
import id.dev.spendless.core.presentation.ui.ObserveAsEvents
import id.dev.spendless.core.presentation.ui.keyboardOpenAsState
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreenRoot(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    viewModel: ExportViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.event) { event ->
        when (event) {
            is ExportEvent.ExportSuccess -> {
                openExportedFile(context, event.data, event.formatEnum)
                onDismissRequest()
            }
        }
    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(0.75f),
        containerColor = sheetBackground,
        tonalElevation = 16.dp,
        sheetState = sheetState,
        shape = RoundedCornerShape(topEnd = 28.dp, topStart = 28.dp),
        onDismissRequest = onDismissRequest,
        dragHandle = null,
        windowInsets = WindowInsets.waterfall
    ) {
        ExportScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    is ExportAction.OnCloseModalSheet -> onDismissRequest()
                    else -> Unit
                }
                viewModel.onAction(action)
            }
        )
    }
}

@Composable
private fun ExportScreen(
    state: ExportState,
    onAction: (ExportAction) -> Unit
) {
    val keyboardOpen by keyboardOpenAsState()
    val errorHeightDp by animateDpAsState(
        if (keyboardOpen) errorHeightOpenKeyboard else errorHeightClosedKeyboard,
        label = "anim_error_height"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ExportAppBar(
                modifier = Modifier.padding(end = 12.dp),
                onCloseClick = {
                    onAction(ExportAction.OnCloseModalSheet)
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.export_range),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            ExportRangeDropDown(
                months = state.monthYearOptions,
                selectedRange = state.selectedExportRange,
                selectedMonth = state.selectedMonthYear,
                onSelectedMonth = {
                    onAction(ExportAction.OnSelectedMonth(it))
                },
                onSelectedRange = {
                    onAction(ExportAction.OnSelectedExportRange(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.export_format),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.W500,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            ExportFormatDropDown(
                selectedFormat = state.selectedExportFormat,
                onSelectedFormat = {
                    onAction(ExportAction.OnSelectedExportFormat(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            SpendLessButton(
                text = if (!state.isLoading) stringResource(R.string.export) else "",
                onClick = {
                    onAction(ExportAction.OnExportClick)
                },
                enable = !state.isLoading && !state.isErrorVisible,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                icon = {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 3.dp
                        )
                    }
                }
            )
        }


        SpendLessErrorContainer(
            isErrorVisible = state.isErrorVisible,
            errorHeightDp = errorHeightDp,
            errorMessage = state.errorMessage?.asString() ?: "",
            keyboardOpen = keyboardOpen,
            modifier = Modifier
                .imePadding()
                .align(Alignment.BottomCenter)
        )
    }
}

private fun openExportedFile(context: Context, uriString: String, format: ExportFormatEnum) {
    try {
        val uri = Uri.parse(uriString)
        val mimeType = when (format) {
            ExportFormatEnum.CSV -> listOf("text/csv", "text/comma-separated-values", "text/plain")
                .firstOrNull { type ->
                    context.packageManager.resolveActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, type)
                        },
                        PackageManager.MATCH_DEFAULT_ONLY
                    ) != null
                } ?: "text/plain"

            ExportFormatEnum.PDF -> "application/pdf"
        }

        val openIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(openIntent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No app found to open this file type",
            Toast.LENGTH_LONG
        ).show()
    } catch (e: SecurityException) {
        Toast.makeText(
            context,
            "Permission denied to open file",
            Toast.LENGTH_LONG
        ).show()
    }
}


@Preview(showBackground = true, name = "Export Screen Preview")
@Composable
fun ExportScreenPreview() {
    SpendLessTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackground)
        ) {
            ExportScreen(
                state = ExportState(isLoading = true),
                onAction = {}
            )
        }
    }
}