package com.ericxc.android.accounting.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ericxc.android.accounting.data.model.AccountEntity

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity): Long

    @Update
    suspend fun update(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT * FROM account ORDER BY sortOrder ASC")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM account WHERE id = :id")
    suspend fun getById(id: Long): AccountEntity?
}
