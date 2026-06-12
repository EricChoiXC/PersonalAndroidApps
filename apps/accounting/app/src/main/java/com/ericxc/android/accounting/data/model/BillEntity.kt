package com.ericxc.android.accounting.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bill")
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: Int,
    val accountId: Long,
    val accountNumber: String = "",
    val amount: Double,
    val tagIds: String = "",
    val remark: String = "",
    val createTime: Long = System.currentTimeMillis(),
    val billDate: Long = System.currentTimeMillis()
)
