package com.personal.accounting.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.personal.accounting.data.model.BillEntity
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
class BillDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: BillDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        dao = database.billDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetById() = runBlocking {
        val bill = BillEntity(type = 0, accountId = 1, amount = 100.0)
        val id = dao.insert(bill)
        assertTrue(id > 0)
        val loaded = dao.getById(id)
        assertNotNull(loaded)
        assertEquals(0, loaded!!.type)
        assertEquals(1, loaded.accountId)
        assertEquals(100.0, loaded.amount, 0.001)
    }

    @Test
    fun getAllReturnsDescOrder() = runBlocking {
        val id1 = dao.insert(BillEntity(type = 0, accountId = 1, amount = 50.0, billDate = 1000))
        val id2 = dao.insert(BillEntity(type = 1, accountId = 2, amount = 200.0, billDate = 2000))
        Thread.sleep(10)
        val list = dao.getAll()
        assertEquals(2, list.size)
        assertTrue(list[0].billDate >= list[1].billDate)
    }

    @Test
    fun updateBill() = runBlocking {
        val id = dao.insert(BillEntity(type = 0, accountId = 1, amount = 100.0, remark = "旧备注"))
        dao.update(BillEntity(id = id, type = 1, accountId = 2, amount = 200.0, remark = "新备注"))
        val loaded = dao.getById(id)
        assertEquals(1, loaded!!.type)
        assertEquals(200.0, loaded.amount, 0.001)
        assertEquals("新备注", loaded.remark)
    }

    @Test
    fun deleteBill() = runBlocking {
        val id = dao.insert(BillEntity(type = 0, accountId = 1, amount = 100.0))
        val loaded = dao.getById(id)
        assertNotNull(loaded)
        dao.delete(loaded!!)
        assertNull(dao.getById(id))
    }

    @Test
    fun queryByType() = runBlocking {
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 50.0))
        dao.insert(BillEntity(type = 1, accountId = 1, amount = 100.0))
        dao.insert(BillEntity(type = 0, accountId = 2, amount = 30.0))

        val expense = dao.query(type = 0)
        assertEquals(2, expense.size)
        expense.forEach { assertEquals(0, it.type) }

        val income = dao.query(type = 1)
        assertEquals(1, income.size)
        assertEquals(100.0, income[0].amount, 0.001)
    }

    @Test
    fun queryByAccountId() = runBlocking {
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 50.0))
        dao.insert(BillEntity(type = 0, accountId = 2, amount = 100.0))

        val bills = dao.query(accountId = 1)
        assertEquals(1, bills.size)
        assertEquals(1, bills[0].accountId)
    }

    @Test
    fun queryByAmountRange() = runBlocking {
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 10.0))
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 50.0))
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 100.0))

        val bills = dao.query(amountMin = 20.0, amountMax = 80.0)
        assertEquals(1, bills.size)
        assertEquals(50.0, bills[0].amount, 0.001)
    }

    @Test
    fun queryByDateRange() = runBlocking {
        val day1 = 1000000L
        val day2 = 2000000L
        val day3 = 3000000L
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 10.0, billDate = day1))
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 20.0, billDate = day2))
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 30.0, billDate = day3))

        val bills = dao.query(startDate = day1, endDate = day2)
        assertEquals(2, bills.size)
    }

    @Test
    fun queryNoFiltersReturnsAll() = runBlocking {
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 10.0))
        dao.insert(BillEntity(type = 1, accountId = 2, amount = 20.0))
        assertEquals(2, dao.query().size)
    }

    @Test
    fun sumByType() = runBlocking {
        val now = System.currentTimeMillis()
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 10.0, billDate = now))
        dao.insert(BillEntity(type = 0, accountId = 1, amount = 20.0, billDate = now))
        dao.insert(BillEntity(type = 1, accountId = 2, amount = 100.0, billDate = now))

        val totalExpense = dao.sumByType(0, now - 1000, now + 1000)
        assertEquals(30.0, totalExpense, 0.001)

        val totalIncome = dao.sumByType(1, now - 1000, now + 1000)
        assertEquals(100.0, totalIncome, 0.001)
    }

    @Test
    fun sumByTypeNoMatchReturnsZero() = runBlocking {
        val total = dao.sumByType(0, 0, 1000)
        assertEquals(0.0, total, 0.001)
    }

    @Test
    fun getByIdReturnsNullForNonExistent() = runBlocking {
        assertNull(dao.getById(999))
    }
}
