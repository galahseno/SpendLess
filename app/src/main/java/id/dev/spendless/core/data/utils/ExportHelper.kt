package id.dev.spendless.core.data.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import id.dev.spendless.core.domain.model.transaction.Transaction
import java.io.File
import java.io.FileOutputStream
import java.time.Instant
import java.time.ZoneId

class ExportHelper(
    private val context: Context,
) {
    fun getDownloadsDirectory(): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "id.dev.spendless"
            ).apply { mkdirs() }
        }
    }

    fun saveFileWithMediaStore(file: File, mimeType: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                contentValues
            )?.also { uri ->
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    file.inputStream().use { it.copyTo(os) }
                }
            }
        } else {
            val destination = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                file.name
            )
            file.copyTo(destination, overwrite = true)
            Uri.fromFile(destination)
        }
    }

    fun createCsvFile(file: File, transactions: List<Transaction>) {
        val writer = file.printWriter()
        writer.use { out ->
            out.println("Transaction Name,Category,Note,Amount,Repeat Interval,Date")
            transactions.sortedByDescending { it.createdAt }.forEach { tx ->
                val localDate = Instant
                    .ofEpochMilli(tx.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                out.println(
                    "${tx.transactionName},${tx.categoryName},${tx.note},${tx.amount}," +
                            "${tx.repeatInterval},${DateFormatter.format(localDate)},"
                )
            }
        }
    }

    fun createPdfFile(file: File, transactions: List<Transaction>) {
        val titleFont = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
        val document = Document().apply {
            setMargins(24f, 24f, 32f, 32f)
            pageSize = PageSize.A4
        }

        val pdf = PdfWriter.getInstance(document, FileOutputStream(file))

        try {
            pdf.setFullCompression()
            document.open()
            document.add(Paragraph("Transaction Data", titleFont))
            document.add(Paragraph(" "))

            val table = PdfPTable(6).apply {
                widthPercentage = 100f
                setWidths(floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f))
                headerRows = 1
                defaultCell.apply {
                    verticalAlignment = Element.ALIGN_CENTER
                    horizontalAlignment = Element.ALIGN_CENTER
                }
            }

            listOf("Transaction Name", "Category", "Note", "Amount", "Repeat Interval", "Date")
                .forEach { header -> table.addCell(createCell(header)) }

            transactions.sortedByDescending { it.createdAt }.forEach { tx ->
                val localDate = Instant.ofEpochMilli(tx.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                listOf(
                    tx.transactionName,
                    tx.categoryName,
                    tx.note,
                    tx.amount,
                    tx.repeatInterval,
                    DateFormatter.format(localDate)
                ).forEach { content -> table.addCell(createCell(content)) }
            }

            document.add(table)
        } finally {
            document.close()
            pdf.close()
        }
    }

    private fun createCell(content: String): PdfPCell {
        return PdfPCell(Phrase(content)).apply {
            horizontalAlignment = Element.ALIGN_CENTER
            verticalAlignment = Element.ALIGN_MIDDLE
            setPadding(8f)
            isUseAscender = true
            paddingLeft = 4f
            paddingRight = 4f
            paddingTop = 8f
            paddingBottom = 8f
        }
    }

}