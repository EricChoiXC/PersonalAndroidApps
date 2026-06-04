package com.personal.accounting.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.personal.accounting.data.model.TagEntity

@Dao
interface TagDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: TagEntity): Long

    @Update
    suspend fun update(tag: TagEntity)

    @Delete
    suspend fun delete(tag: TagEntity)

    @Query("SELECT * FROM tag ORDER BY sortOrder ASC")
    suspend fun getAll(): List<TagEntity>

    @Query("SELECT * FROM tag WHERE id = :id")
    suspend fun getById(id: Long): TagEntity?
}
