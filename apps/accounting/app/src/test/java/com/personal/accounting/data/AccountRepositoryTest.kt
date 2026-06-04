package com.personal.accounting.data

import com.personal.accounting.data.local.AccountDao
import com.personal.accounting.data.model.AccountEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AccountRepositoryTest {

    private val mockDao: AccountDao = mock()
    private lateinit var repository: AccountRepository

    @Before
    fun setUp() {
        repository = AccountRepository(mockDao)
    }

    @Test
    fun getAllDelegatesToDao() {
        runBlocking {
            val expected = listOf(AccountEntity(name = "a"), AccountEntity(name = "b"))
            whenever(mockDao.getAll()).thenReturn(expected)

            val result = repository.getAll()

            assertEquals(expected, result)
            verify(mockDao).getAll()
        }
    }

    @Test
    fun getByIdDelegatesToDao() {
        runBlocking {
            val expected = AccountEntity(id = 1, name = "test")
            whenever(mockDao.getById(1)).thenReturn(expected)

            val result = repository.getById(1)

            assertEquals(expected, result)
            verify(mockDao).getById(1)
        }
    }

    @Test
    fun insertDelegatesToDao() {
        runBlocking {
            val account = AccountEntity(name = "new")
            whenever(mockDao.insert(account)).thenReturn(42L)

            val result = repository.insert(account)

            assertEquals(42L, result)
            verify(mockDao).insert(account)
        }
    }

    @Test
    fun updateDelegatesToDao() {
        runBlocking {
            val account = AccountEntity(id = 1, name = "updated")

            repository.update(account)

            verify(mockDao).update(account)
        }
    }

    @Test
    fun deleteDelegatesToDao() {
        runBlocking {
            val account = AccountEntity(id = 1, name = "delete")

            repository.delete(account)

            verify(mockDao).delete(account)
        }
    }

    @Test
    fun getByIdReturnsNullWhenNotFound() {
        runBlocking {
            whenever(mockDao.getById(999)).thenReturn(null)

            val result = repository.getById(999)

            assertEquals(null, result)
            verify(mockDao).getById(999)
        }
    }
}
