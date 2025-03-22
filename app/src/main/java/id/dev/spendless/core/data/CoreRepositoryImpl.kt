package id.dev.spendless.core.data

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
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
import id.dev.spendless.core.data.database.dao.PreferencesDao
import id.dev.spendless.core.data.database.dao.TransactionDao
import id.dev.spendless.core.data.database.dao.UserDao
import id.dev.spendless.core.data.database.entity.PreferencesEntity
import id.dev.spendless.core.data.database.entity.TransactionEntity
import id.dev.spendless.core.data.utils.DateFormatter
import id.dev.spendless.core.domain.CoreRepository
import id.dev.spendless.core.domain.EncryptionService
import id.dev.spendless.core.domain.SettingPreferences
import id.dev.spendless.core.domain.model.export.ExportFormatEnum
import id.dev.spendless.core.domain.model.export.ExportRangeEnum
import id.dev.spendless.core.domain.model.export.MonthYear
import id.dev.spendless.core.domain.model.transaction.AddTransactionModel
import id.dev.spendless.core.domain.model.transaction.Transaction
import id.dev.spendless.core.domain.util.DataError
import id.dev.spendless.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class CoreRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val userDao: UserDao,
    private val preferencesDao: PreferencesDao,
    private val settingPreferences: SettingPreferences,
    private val encryptionService: EncryptionService,
    private val context: Context
) : CoreRepository {
    override suspend fun createTransaction(addTransactionModel: AddTransactionModel): Result<Unit, DataError.Local> =
        withContext(Dispatchers.IO) {
            try {
                val userId = settingPreferences.getUserId().first()
                val (transactionNameEncrypted, transactionNameIv) = encryptionService.encrypt(
                    addTransactionModel.transactionName
                )
                val (categoryEmojiEncrypted, categoryEmojiIv) = encryptionService.encrypt(
                    addTransactionModel.categoryEmoji
                )
                val (categoryNameEncrypted, categoryNameIv) = encryptionService.encrypt(
                    addTransactionModel.categoryName
                )
                val (amountEncrypted, amountIv) = encryptionService.encrypt(addTransactionModel.amount.toString())
                val (noteEncrypted, noteIv) = encryptionService.encrypt(addTransactionModel.note)
                val (repeatEncrypted, repeatIv) = encryptionService.encrypt(addTransactionModel.repeat)

                val transactionEntity = TransactionEntity(
                    userId = userId,
                    transactionName = transactionNameEncrypted,
                    categoryEmoji = categoryEmojiEncrypted,
                    categoryName = categoryNameEncrypted,
                    amount = amountEncrypted,
                    note = noteEncrypted,
                    createdAt = addTransactionModel.createdAt,
                    repeat = repeatEncrypted,
                    transactionNameIv = transactionNameIv,
                    categoryEmojiIv = categoryEmojiIv,
                    categoryNameIv = categoryNameIv,
                    amountIv = amountIv,
                    noteIv = noteIv,
                    repeatIv = repeatIv
                )
                val transactionId =
                    transactionDao.createTransaction(transactionEntity)

                if (transactionId == -1L) {
                    return@withContext Result.Error(DataError.Local.ERROR_PROSES)
                }
                return@withContext Result.Success(Unit)

            } catch (_: Exception) {
                coroutineContext.ensureActive()
                return@withContext Result.Error(DataError.Local.ERROR_PROSES)
            }
        }

    override suspend fun checkSessionPin(
        userId: Int,
        pin: String
    ): Result<Boolean, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val user =
                userDao.getUserById(userId) ?: return@withContext Result.Error(DataError.Local.USER_NOT_EXIST)
            if (encryptionService.decrypt(user.pin, user.iv) == pin) {
                settingPreferences.updateLatestTimeStamp()
                settingPreferences.changeSession(false)
            }

            return@withContext Result.Success(encryptionService.decrypt(user.pin, user.iv) == pin)
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return@withContext Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    override suspend fun exportTransactions(
        range: ExportRangeEnum,
        format: ExportFormatEnum,
        specificMonth: MonthYear
    ): Result<String, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val userId = settingPreferences.getUserId().first()

            val transactions = when (range) {
                ExportRangeEnum.ALL_DATA -> transactionDao.getAllTransactions(userId).first()
                ExportRangeEnum.LAST_THREE_MONTHS -> {
                    val threeMonthsAgo = LocalDate.now().minusMonths(3)
                    transactionDao.getTransactionsAfter(
                        userId,
                        threeMonthsAgo.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
                    )
                }

                ExportRangeEnum.LAST_MONTH -> {
                    val lastMonth = LocalDate.now().minusMonths(1)
                    transactionDao.getTransactionsByMonth(
                        userId,
                        lastMonth.year.toString(),
                        "%02d".format(lastMonth.monthValue)
                    )
                }

                ExportRangeEnum.CURRENT_MONTH -> {
                    val current = LocalDate.now()
                    transactionDao.getTransactionsByMonth(
                        userId, current.year.toString(),
                        "%02d".format(current.monthValue)
                    )
                }

                ExportRangeEnum.SPECIFIC_MONTH -> {
                    transactionDao.getTransactionsByMonth(
                        userId,
                        specificMonth.year.toString(),
                        "%02d".format(specificMonth.month)
                    )
                }
            }

            if (transactions.isEmpty()) {
                return@withContext Result.Error(DataError.Local.NO_DATA)
            }

            val decryptedTransactions = transactions.map { decryptTransactions(it) }

            val fileName = when (format) {
                ExportFormatEnum.CSV -> "transactions_${System.currentTimeMillis()}.csv"
                ExportFormatEnum.PDF -> "transactions_${System.currentTimeMillis()}.pdf"
            }

            val downloadsDir = getDownloadsDirectory(context)
            val file = File(downloadsDir, fileName)

            when (format) {
                ExportFormatEnum.CSV -> createCsvFile(file, decryptedTransactions)
                ExportFormatEnum.PDF -> createPdfFile(file, decryptedTransactions)
            }

            val uri = saveFileWithMediaStore(
                context,
                file,
                when (format) {
                    ExportFormatEnum.CSV -> "text/csv"
                    ExportFormatEnum.PDF -> "application/pdf"
                }
            )

            file.delete()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    null,
                    null
                )
            }
            return@withContext Result.Success(uri.toString())
        } catch (e: Exception) {
            coroutineContext.ensureActive()
            return@withContext Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    override suspend fun logout(): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {
        try {
            val userid = settingPreferences.getUserId().first()
            val userSession = settingPreferences.getUserSession().first()
            val userSecurity = settingPreferences.getUserSecurity().first()

            val preferencesEntity = PreferencesEntity(
                userId = userid,
                expensesFormat = userSession.expensesFormat,
                currencySymbol = userSession.currencySymbol,
                decimalSeparator = userSession.decimalSeparator,
                thousandSeparator = userSession.thousandSeparator,
                biometricPromptEnable = userSecurity.biometricPromptEnable,
                sessionExpiryDuration = userSecurity.sessionExpiryDuration,
                lockedOutDuration = userSecurity.lockedOutDuration,
            )

            val result = preferencesDao.insertPreferences(preferencesEntity)

            if (result == -1L) {
                return@withContext Result.Error(DataError.Local.ERROR_PROSES)
            }
            settingPreferences.logout()

            return@withContext Result.Success(Unit)
        } catch (_: Exception) {
            coroutineContext.ensureActive()
            return@withContext Result.Error(DataError.Local.ERROR_PROSES)
        }
    }

    private fun saveFileWithMediaStore(context: Context, file: File, mimeType: String): Uri? {
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

    private fun getDownloadsDirectory(context: Context): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "id.dev.spendless"
            ).apply { mkdirs() }
        }
    }

    private fun createCsvFile(file: File, transactions: List<Transaction>) {
        val writer = file.printWriter()
        writer.use { out ->
            // Write CSV header
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

    private fun createPdfFile(file: File, transactions: List<Transaction>) {
        val titleFont = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
        val document = createDocument()
        val pdf = PdfWriter.getInstance(document, FileOutputStream(file))

        try {
            pdf.setFullCompression()

            document.open()
            document.add(Paragraph("Transaction Data", titleFont))
            document.add(Paragraph(" "))

            val transactionNameWidth = 1f
            val categoryWidth = 1f
            val noteWidth = 1f
            val amountWidth = 1f
            val repeatWidth = 1f
            val dateWidth = 1f
            val columnWidth = floatArrayOf(
                transactionNameWidth,
                categoryWidth,
                noteWidth,
                amountWidth,
                repeatWidth,
                dateWidth
            )

            val table = createTable(columnWidth.size, columnWidth)
            val tableHeaderContent =
                listOf("Transaction Name", "Category", "Note", "Amount", "Repeat Interval", "Date")
            tableHeaderContent.forEach {
                val cell = createCell(it)
                table.addCell(cell)
            }

            transactions.sortedByDescending { it.createdAt }.forEach { tx ->
                val localDate = Instant.ofEpochMilli(tx.createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                val transactionNameCell = createCell(tx.transactionName)
                table.addCell(transactionNameCell)

                val categoryNameCell = createCell(tx.categoryName)
                table.addCell(categoryNameCell)

                val noteCell = createCell(tx.note)
                table.addCell(noteCell)

                val amountCell = createCell(tx.amount)
                table.addCell(amountCell)

                val repeatCell = createCell(tx.repeatInterval)
                table.addCell(repeatCell)

                val dateCell = createCell(DateFormatter.format(localDate))
                table.addCell(dateCell)
            }
            document.add(table)

        } catch (e: Exception) {
            throw IOException("Error generating PDF", e)
        } finally {
            document.close()
            pdf.close()
        }
    }

    private fun createDocument(): Document {
        val document = Document()
        document.setMargins(24f, 24f, 32f, 32f)
        document.pageSize = PageSize.A4
        return document
    }

    private fun createTable(column: Int, columnWidth: FloatArray): PdfPTable {
        val table = PdfPTable(column)
        table.widthPercentage = 100f
        table.setWidths(columnWidth)
        table.headerRows = 1
        table.defaultCell.verticalAlignment = Element.ALIGN_CENTER
        table.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
        return table
    }

    private fun createCell(content: String): PdfPCell {
        val cell = PdfPCell(Phrase(content))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.verticalAlignment = Element.ALIGN_MIDDLE

        cell.setPadding(8f)
        cell.isUseAscender = true
        cell.paddingLeft = 4f
        cell.paddingRight = 4f
        cell.paddingTop = 8f
        cell.paddingBottom = 8f
        return cell
    }

    private fun decryptTransactions(transactionEntity: TransactionEntity): Transaction {
        val transactionName = encryptionService.decrypt(
            transactionEntity.transactionName,
            transactionEntity.transactionNameIv
        )
        val categoryEmoji = encryptionService.decrypt(
            transactionEntity.categoryEmoji,
            transactionEntity.categoryEmojiIv
        )
        val categoryName = encryptionService.decrypt(
            transactionEntity.categoryName,
            transactionEntity.categoryNameIv
        )
        val amount = encryptionService.decrypt(
            transactionEntity.amount, transactionEntity.amountIv
        )

        val note = encryptionService.decrypt(transactionEntity.note, transactionEntity.noteIv)
        val repeat = encryptionService.decrypt(transactionEntity.repeat, transactionEntity.repeatIv)

        return Transaction(
            id = transactionEntity.id,
            transactionName = transactionName,
            categoryEmoji = categoryEmoji,
            categoryName = categoryName,
            amount = amount,
            note = note,
            createdAt = transactionEntity.createdAt,
            repeatInterval = repeat
        )
    }
}