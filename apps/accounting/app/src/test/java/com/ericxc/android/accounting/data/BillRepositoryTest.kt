package com.ericxc.android.accounting.data

import com.ericxc.android.accounting.data.local.BillDao
import com.ericxc.android.accounting.data.model.BillEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class BillRepositoryTest {

    private val mockDao: BillDao = mock()
    private lateinit var repository: BillRepository

    @Before
    fun setUp() {
        repository = BillRepository(mockDao)
    }

    @Test
    fun getAllDelegatesToDao() {
        runBlocking {
            val expected = listOf(
                BillEntity(type = 0, accountId = 1, amount = 50.0),
                BillEntity(type = 1, accountId = 2, amount = 100.0)
            )
            whenever(mockDao.getAll()).thenReturn(expected)

            val result = repository.getAll()

            assertEquals(expected, result)
            verify(mockDao).getAll()
        }
    }

    @Test
    fun getByIdDelegatesToDao() {
        runBlocking {
            val expected = BillEntity(id = 1, type = 0, accountId = 1, amount = 50.0)
            whenever(mockDao.getById(1)).thenReturn(expected)

            val result = repository.getById(1)

            assertEquals(expected, result)
            verify(mockDao).getById(1)
        }
    }

    @Test
    fun insertDelegatesToDao() {
        runBlocking {
            val bill = BillEntity(type = 0, accountId = 1, amount = 100.0)
            whenever(mockDao.insert(bill)).thenReturn(42L)

            val result = repository.insert(bill)

            assertEquals(42L, result)
            verify(mockDao).insert(bill)
        }
    }

    @Test
    fun deleteDelegatesToDao() {
        runBlocking {
            val bill = BillEntity(id = 1, type = 0, accountId = 1, amount = 50.0)

            repository.delete(bill)

            verify(mockDao).delete(bill)
        }
    }

    @Test
    fun queryWithoutTagsDelegatesToDao() {
        runBlocking {
            val expected = listOf(BillEntity(type = 0, accountId = 1, amount = 50.0))
            whenever(mockDao.query(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
                .thenReturn(expected)

            val result = repository.query(type = 0, accountId = 1)

            assertEquals(expected, result)
            verify(mockDao).query(0, 1, null, null, null, null)
        }
    }

    @Test
    fun queryWithTagsFiltersInMemory() {
        runBlocking {
            val billA = BillEntity(id = 1, type = 0, accountId = 1, amount = 50.0, tagIds = "1,2")
            val billB = BillEntity(id = 2, type = 0, accountId = 1, amount = 30.0, tagIds = "3")
            val billC = BillEntity(id = 3, type = 0, accountId = 1, amount = 20.0, tagIds = "1,3")
            val daoResult = listOf(billA, billB, billC)

            whenever(mockDao.query(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
                .thenReturn(daoResult)

            val result = repository.query(tagIds = listOf(1L, 2L))
            assertEquals(2, result.size)
            assertEquals(1, result[0].id)
            assertEquals(3, result[1].id)
        }
    }

    @Test
    fun queryWithEmptyTagIdsReturnsAll() {
        runBlocking {
            val bills = listOf(
                BillEntity(id = 1, type = 0, accountId = 1, amount = 50.0, tagIds = "1"),
                BillEntity(id = 2, type = 0, accountId = 1, amount = 30.0, tagIds = "2")
            )
            whenever(mockDao.query(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
                .thenReturn(bills)

            val result = repository.query(tagIds = emptyList())
            assertEquals(2, result.size)
        }
    }

    @Test
    fun sumByTypeDelegatesToDao() {
        runBlocking {
            whenever(mockDao.sumByType(0, 1000L, 2000L)).thenReturn(150.0)

            val result = repository.sumByType(0, 1000L, 2000L)

            assertEquals(150.0, result, 0.001)
            verify(mockDao).sumByType(0, 1000L, 2000L)
        }
    }
}
