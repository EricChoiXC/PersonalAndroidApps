package com.personal.accounting.data

import com.personal.accounting.data.local.AccountDao
import com.personal.accounting.data.model.AccountEntity

class AccountRepository(private val accountDao: AccountDao) {

    suspend fun getAll(): List<AccountEntity> = accountDao.getAll()

    suspend fun getById(id: Long): AccountEntity? = accountDao.getById(id)

    suspend fun insert(account: AccountEntity): Long = accountDao.insert(account)

    suspend fun update(account: AccountEntity) = accountDao.update(account)

    suspend fun delete(account: AccountEntity) = accountDao.delete(account)
}
