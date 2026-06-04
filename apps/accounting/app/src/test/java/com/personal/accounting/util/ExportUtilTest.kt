package com.personal.accounting.util

import android.os.Build
import com.personal.accounting.data.model.AccountEntity
import com.personal.accounting.data.model.BillEntity
import com.personal.accounting.data.model.TagEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import java.io.FileInputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ExportUtilTest {

    @Test
    fun exportCreatesXlsxFile() {
        val bills = listOf(
            BillEntity(type = 0, accountId = 1, amount = 50.0, tagIds = "1", remark = "午餐", createTime = 1000),
            BillEntity(type = 1, accountId = 2, amount = 5000.0, tagIds = "2", remark = "工资", createTime = 2000)
        )
        val accounts = listOf(
            AccountEntity(id = 1, name = "储蓄卡", accountNumber = "6222", sortOrder = 0),
            AccountEntity(id = 2, name = "支付宝余额", accountNumber = "", sortOrder = 1)
        )
        val tags = listOf(
            TagEntity(id = 1, name = "餐饮", sortOrder = 0),
            TagEntity(id = 2, name = "转账", sortOrder = 1)
        )

        val path = ExportUtil.export(RuntimeEnvironment.getApplication(), bills, accounts, tags)

        assertNotNull(path)
        assertTrue(path!!.endsWith(".xlsx"))
        val file = File(path)
        assertTrue("File should exist", file.exists())
        assertTrue("File should have content", file.length() > 0)

        // Verify it's a valid xlsx (ZIP) file
        FileInputStream(file).use { input ->
            val header = ByteArray(4)
            input.read(header)
            // PK\x03\x04 is the ZIP file header
            assertTrue("Should be a valid ZIP/xlsx file", header[0] == 0x50.toByte() && header[1] == 0x4B.toByte())
        }

        file.delete()
    }

    @Test
    fun exportWithEmptyBillsCreatesHeaderOnly() {
        val path = ExportUtil.export(
            RuntimeEnvironment.getApplication(),
            emptyList(),
            listOf(AccountEntity(id = 1, name = "储蓄卡")),
            listOf(TagEntity(id = 1, name = "餐饮"))
        )

        assertNotNull(path)
        val file = File(path!!)
        assertTrue(file.exists())
        assertTrue(file.length() > 0)
        file.delete()
    }

    @Test
    fun exportWithMultipleBillsCorrectRowCount() {
        val bills = List(5) { i ->
            BillEntity(
                id = i.toLong(),
                type = i % 2,
                accountId = 1,
                amount = (i + 1) * 10.0,
                tagIds = if (i % 2 == 0) "1" else "",
                createTime = System.currentTimeMillis()
            )
        }
        val accounts = listOf(AccountEntity(id = 1, name = "储蓄卡"))
        val tags = listOf(TagEntity(id = 1, name = "餐饮"))

        val path = ExportUtil.export(RuntimeEnvironment.getApplication(), bills, accounts, tags)

        assertNotNull(path)
        val file = File(path!!)
        assertTrue("File should exist", file.exists())
        file.delete()
    }
}
