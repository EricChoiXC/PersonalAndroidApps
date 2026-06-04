package com.personal.accounting.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.personal.accounting.data.model.TagEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TagDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: TagDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.tagDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetById() = runBlocking {
        val tag = TagEntity(name = "餐饮", sortOrder = 1)
        val id = dao.insert(tag)
        assertTrue(id > 0)
        val loaded = dao.getById(id)
        assertNotNull(loaded)
        assertEquals("餐饮", loaded!!.name)
        assertEquals(1, loaded.sortOrder)
    }

    @Test
    fun getAllReturnsAllTagsInSortOrder() = runBlocking {
        dao.insert(TagEntity(name = "标签B", sortOrder = 1))
        dao.insert(TagEntity(name = "标签A", sortOrder = 0))
        val list = dao.getAll()
        assertEquals(2, list.size)
        assertEquals("标签A", list[0].name)
        assertEquals("标签B", list[1].name)
    }

    @Test
    fun updateTag() = runBlocking {
        val id = dao.insert(TagEntity(name = "旧标签", sortOrder = 0))
        dao.update(TagEntity(id = id, name = "新标签", sortOrder = 1))
        val loaded = dao.getById(id)
        assertEquals("新标签", loaded!!.name)
        assertEquals(1, loaded.sortOrder)
    }

    @Test
    fun deleteTag() = runBlocking {
        val id = dao.insert(TagEntity(name = "待删除", sortOrder = 0))
        val loaded = dao.getById(id)
        assertNotNull(loaded)
        dao.delete(loaded!!)
        assertNull(dao.getById(id))
    }

    @Test
    fun getByIdReturnsNullForNonExistent() = runBlocking {
        assertNull(dao.getById(999))
    }

    @Test
    fun insertMultipleTagsAndCount() = runBlocking {
        repeat(5) { dao.insert(TagEntity(name = "标签$it", sortOrder = it)) }
        assertEquals(5, dao.getAll().size)
    }
}
