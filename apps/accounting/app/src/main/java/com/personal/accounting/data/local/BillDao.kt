package com.personal.accounting.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personal.accounting.data.model.BillEntity

@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: BillEntity): Long

    @Update
    suspend fun update(bill: BillEntity)

    @Delete
    suspend fun delete(bill: BillEntity)

    @Query("SELECT * FROM bill WHERE id = :id")
    suspend fun getById(id: Long): BillEntity?

    @Query("SELECT * FROM bill ORDER BY createTime DESC")
    suspend fun getAll(): List<BillEntity>

    @Query("""
        SELECT * FROM bill
        WHERE (:type IS NULL OR type = :type)
        AND (:accountId IS NULL OR accountId = :accountId)
        AND (:amountMin IS NULL OR amount >= :amountMin)
        AND (:amountMax IS NULL OR amount <= :amountMax)
        AND (:startDate IS NULL OR createTime >= :startDate)
        AND (:endDate IS NULL OR createTime <= :endDate)
        ORDER BY createTime DESC
    """)
    suspend fun query(
        type: Int? = null,
        accountId: Long? = null,
        amountMin: Double? = null,
        amountMax: Double? = null,
        startDate: Long? = null,
        endDate: Long? = null
    ): List<BillEntity>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM bill WHERE type = :type AND createTime >= :startDate AND createTime <= :endDate")
    suspend fun sumByType(type: Int, startDate: Long, endDate: Long): Double
}
