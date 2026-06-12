package com.ericxc.android.accounting.data

import com.ericxc.android.accounting.data.local.AccountDao
import com.ericxc.android.accounting.data.model.AccountEntity

class AccountRepository(private val accountDao: AccountDao) {

    suspend fun getAll(): List<AccountEntity> = accountDao.getAll()

    suspend fun getById(id: Long): AccountEntity? = accountDao.getById(id)

    suspend fun insert(account: AccountEntity): Long = accountDao.insert(account)

    suspend fun update(account: AccountEntity) = accountDao.update(account)

    suspend fun delete(account: AccountEntity) = accountDao.delete(account)
}
