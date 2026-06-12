package com.ericxc.android.accounting.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ericxc.android.accounting.data.model.AccountEntity
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
class AccountDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: AccountDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.accountDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetById() = runBlocking {
        val account = AccountEntity(name = "测试账户", accountNumber = "123456", sortOrder = 1)
        val id = dao.insert(account)
        assertTrue(id > 0)
        val loaded = dao.getById(id)
        assertNotNull(loaded)
        assertEquals("测试账户", loaded!!.name)
        assertEquals("123456", loaded.accountNumber)
        assertEquals(1, loaded.sortOrder)
    }

    @Test
    fun getAllReturnsAllAccounts() = runBlocking {
        dao.insert(AccountEntity(name = "账户A", sortOrder = 0))
        dao.insert(AccountEntity(name = "账户B", sortOrder = 1))
        val list = dao.getAll()
        assertEquals(2, list.size)
        assertEquals("账户A", list[0].name)
        assertEquals("账户B", list[1].name)
    }

    @Test
    fun getAllReturnsAccountsInSortOrder() = runBlocking {
        dao.insert(AccountEntity(name = "第二", sortOrder = 1))
        dao.insert(AccountEntity(name = "第一", sortOrder = 0))
        val list = dao.getAll()
        assertEquals(2, list.size)
        assertEquals("第一", list[0].name)
        assertEquals("第二", list[1].name)
    }

    @Test
    fun updateAccount() = runBlocking {
        val id = dao.insert(AccountEntity(name = "旧名", accountNumber = "000", sortOrder = 0))
        dao.update(AccountEntity(id = id, name = "新名", accountNumber = "999", sortOrder = 1))
        val loaded = dao.getById(id)
        assertEquals("新名", loaded!!.name)
        assertEquals("999", loaded.accountNumber)
        assertEquals(1, loaded.sortOrder)
    }

    @Test
    fun deleteAccount() = runBlocking {
        val id = dao.insert(AccountEntity(name = "待删除", sortOrder = 0))
        val loaded = dao.getById(id)
        assertNotNull(loaded)
        dao.delete(loaded!!)
        assertNull(dao.getById(id))
    }

    @Test
    fun getByIdReturnsNullForNonExistent() = runBlocking {
        val loaded = dao.getById(999)
        assertNull(loaded)
    }

    @Test
    fun insertWithEmptyAccountNumber() = runBlocking {
        val id = dao.insert(AccountEntity(name = "无号账户", sortOrder = 0))
        val loaded = dao.getById(id)
        assertEquals("", loaded!!.accountNumber)
    }
}
