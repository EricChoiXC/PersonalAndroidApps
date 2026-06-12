package com.ericxc.android.accounting.data

import com.ericxc.android.accounting.data.local.BillDao
import com.ericxc.android.accounting.data.model.BillEntity

class BillRepository(private val billDao: BillDao) {

    suspend fun getAll(): List<BillEntity> = billDao.getAll()

    suspend fun getById(id: Long): BillEntity? = billDao.getById(id)

    suspend fun insert(bill: BillEntity): Long = billDao.insert(bill)

    suspend fun delete(bill: BillEntity) = billDao.delete(bill)

    suspend fun query(
        type: Int? = null,
        accountId: Long? = null,
        amountMin: Double? = null,
        amountMax: Double? = null,
        tagIds: List<Long>? = null,
        startDate: Long? = null,
        endDate: Long? = null
    ): List<BillEntity> {
        val bills = billDao.query(type, accountId, amountMin, amountMax, startDate, endDate)
        if (tagIds.isNullOrEmpty()) return bills
        return bills.filter { bill ->
            val billTagIds = bill.tagIds.split(",").mapNotNull { it.toLongOrNull() }.toSet()
            tagIds.any { it in billTagIds }
        }
    }

    suspend fun sumByType(type: Int, startDate: Long, endDate: Long): Double {
        return billDao.sumByType(type, startDate, endDate)
    }
}
