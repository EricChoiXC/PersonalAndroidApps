package com.ericxc.android.accounting.data

import com.ericxc.android.accounting.data.local.TagDao
import com.ericxc.android.accounting.data.model.TagEntity

class TagRepository(private val tagDao: TagDao) {

    suspend fun getAll(): List<TagEntity> = tagDao.getAll()

    suspend fun getById(id: Long): TagEntity? = tagDao.getById(id)

    suspend fun insert(tag: TagEntity): Long = tagDao.insert(tag)

    suspend fun update(tag: TagEntity) = tagDao.update(tag)

    suspend fun delete(tag: TagEntity) = tagDao.delete(tag)
}
