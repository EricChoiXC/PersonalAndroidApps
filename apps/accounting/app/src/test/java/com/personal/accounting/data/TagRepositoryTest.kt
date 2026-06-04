package com.personal.accounting.data

import com.personal.accounting.data.local.TagDao
import com.personal.accounting.data.model.TagEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TagRepositoryTest {

    private val mockDao: TagDao = mock()
    private lateinit var repository: TagRepository

    @Before
    fun setUp() {
        repository = TagRepository(mockDao)
    }

    @Test
    fun getAllDelegatesToDao() {
        runBlocking {
            val expected = listOf(TagEntity(name = "餐饮"), TagEntity(name = "交通"))
            whenever(mockDao.getAll()).thenReturn(expected)

            val result = repository.getAll()

            assertEquals(expected, result)
            verify(mockDao).getAll()
        }
    }

    @Test
    fun getByIdDelegatesToDao() {
        runBlocking {
            val expected = TagEntity(id = 1, name = "餐饮")
            whenever(mockDao.getById(1)).thenReturn(expected)

            val result = repository.getById(1)

            assertEquals(expected, result)
            verify(mockDao).getById(1)
        }
    }

    @Test
    fun insertDelegatesToDao() {
        runBlocking {
            val tag = TagEntity(name = "新标签")
            whenever(mockDao.insert(tag)).thenReturn(42L)

            val result = repository.insert(tag)

            assertEquals(42L, result)
            verify(mockDao).insert(tag)
        }
    }

    @Test
    fun updateDelegatesToDao() {
        runBlocking {
            val tag = TagEntity(id = 1, name = "updated")

            repository.update(tag)

            verify(mockDao).update(tag)
        }
    }

    @Test
    fun deleteDelegatesToDao() {
        runBlocking {
            val tag = TagEntity(id = 1, name = "delete")

            repository.delete(tag)

            verify(mockDao).delete(tag)
        }
    }
}
