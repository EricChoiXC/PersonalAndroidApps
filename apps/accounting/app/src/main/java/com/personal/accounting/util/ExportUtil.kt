package com.personal.accounting.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.personal.accounting.Config
import com.personal.accounting.data.model.AccountEntity
import com.personal.accounting.data.model.BillEntity
import com.personal.accounting.data.model.TagEntity
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtil {

    fun export(
        context: Context,
        bills: List<BillEntity>,
        accounts: List<AccountEntity>,
        tags: List<TagEntity>
    ): String? {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("账单")

        val headers = arrayOf("序号", "类型", "操作账户", "账户号", "金额", "标签", "备注", "账单时间")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { i, h -> headerRow.createCell(i).setCellValue(h) }

        val dateFormat = SimpleDateFormat(Config.DATE_FORMAT_DISPLAY, Locale.getDefault())
        val accountMap = accounts.associateBy { it.id }
        val tagMap = tags.associateBy { it.id }

        bills.forEachIndexed { index, bill ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue((index + 1).toDouble())
            row.createCell(1).setCellValue(if (bill.type == 0) "出账" else "入账")
            row.createCell(2).setCellValue(accountMap[bill.accountId]?.name ?: "")
            row.createCell(3).setCellValue(bill.accountNumber)
            row.createCell(4).setCellValue(bill.amount)
            val tagNames = bill.tagIds.split(",").mapNotNull {
                it.toLongOrNull()?.let { id -> tagMap[id]?.name }
            }.joinToString("、")
            row.createCell(5).setCellValue(tagNames)
            row.createCell(6).setCellValue(bill.remark)
            row.createCell(7).setCellValue(dateFormat.format(Date(bill.billDate)))
        }

        for (i in headers.indices) sheet.autoSizeColumn(i)

        val fileName = "${Config.EXPORT_FILE_PREFIX}${
            SimpleDateFormat(Config.DATE_FORMAT_EXPORT, Locale.getDefault()).format(Date())
        }${Config.EXPORT_FILE_SUFFIX}"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(context, workbook, fileName)
        } else {
            saveToLegacyStorage(context, workbook, fileName)
        }
    }

    @SuppressLint("NewApi")
    private fun saveViaMediaStore(
        context: Context,
        workbook: XSSFWorkbook,
        fileName: String
    ): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/${Config.EXPORT_DIR}")
        }
        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues
        )
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { os ->
                workbook.write(os)
            }
        }
        workbook.close()
        return "${Environment.DIRECTORY_DOWNLOADS}/${Config.EXPORT_DIR}/${fileName}"
    }

    private fun saveToLegacyStorage(
        context: Context,
        workbook: XSSFWorkbook,
        fileName: String
    ): String? {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Config.EXPORT_DIR
        )
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { os -> workbook.write(os) }
        workbook.close()
        return file.absolutePath
    }
}
