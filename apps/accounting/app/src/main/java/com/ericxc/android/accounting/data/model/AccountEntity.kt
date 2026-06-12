package com.ericxc.android.accounting.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val accountNumber: String = "",
    val sortOrder: Int = 0
)
