package com.ericxc.android.accounting.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.ericxc.android.accounting.Config
import com.ericxc.android.accounting.data.model.AccountEntity
import com.ericxc.android.accounting.data.model.BillEntity
import com.ericxc.android.accounting.data.model.TagEntity
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
        val dateFormat = SimpleDateFormat(Config.DATE_FORMAT_DISPLAY, Locale.getDefault())
        val accountMap = accounts.associateBy { it.id }
        val tagMap = tags.associateBy { it.id }

        val headers = arrayOf("序号", "类型", "操作账户", "账户号", "金额", "标签", "备注", "账单时间")

        val rows: List<Array<Any>> = bills.mapIndexed { index, bill ->
            val tagNames = bill.tagIds.split(",").mapNotNull {
                it.toLongOrNull()?.let { id -> tagMap[id]?.name }
            }.joinToString("、")
            arrayOf<Any>(
                index + 1L,
                if (bill.type == 0) "出账" else "入账",
                accountMap[bill.accountId]?.name ?: "",
                bill.accountNumber,
                bill.amount,
                tagNames,
                bill.remark,
                dateFormat.format(Date(bill.billDate))
            )
        }

        val fileName = "${Config.EXPORT_FILE_PREFIX}${
            SimpleDateFormat(Config.DATE_FORMAT_EXPORT, Locale.getDefault()).format(Date())
        }${Config.EXPORT_FILE_SUFFIX}"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(context, headers, rows, fileName)
        } else {
            saveToLegacyStorage(headers, rows, fileName)
        }
    }

    @SuppressLint("NewApi")
    private fun saveViaMediaStore(
        context: Context,
        headers: Array<String>,
        rows: List<Array<Any>>,
        fileName: String
    ): String? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(
                MediaStore.Downloads.MIME_TYPE,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
            put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/${Config.EXPORT_DIR}")
        }
        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues
        )
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { os ->
                XlsxWriter.write("账单", headers, rows, os)
            }
        }
        return "${Environment.DIRECTORY_DOWNLOADS}/${Config.EXPORT_DIR}/${fileName}"
    }

    private fun saveToLegacyStorage(
        headers: Array<String>,
        rows: List<Array<Any>>,
        fileName: String
    ): String? {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            Config.EXPORT_DIR
        )
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { os ->
            XlsxWriter.write("账单", headers, rows, os)
        }
        return file.absolutePath
    }
}
