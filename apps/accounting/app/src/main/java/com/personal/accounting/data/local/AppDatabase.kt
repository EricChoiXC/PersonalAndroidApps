package com.personal.accounting.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.personal.accounting.Config
import com.personal.accounting.data.model.AccountEntity
import com.personal.accounting.data.model.BillEntity
import com.personal.accounting.data.model.TagEntity

@Database(
    entities = [AccountEntity::class, TagEntity::class, BillEntity::class],
    version = Config.DB_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun tagDao(): TagDao
    abstract fun billDao(): BillDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Config.DB_NAME
                ).addCallback(sDefaultDataCallback)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }

        private val sDefaultDataCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val defaultAccounts = listOf("储蓄卡", "信用卡", "支付宝余额", "微信钱包", "其他")
                defaultAccounts.forEachIndexed { i, name ->
                    db.execSQL(
                        "INSERT INTO account (name, accountNumber, sortOrder) VALUES (?, '', ?)",
                        arrayOf(name, i)
                    )
                }
                val defaultTags = listOf(
                    "餐饮", "交通", "服饰", "转账", "生活水电",
                    "电子用品", "购物", "大宗商品", "其他"
                )
                defaultTags.forEachIndexed { i, name ->
                    db.execSQL(
                        "INSERT INTO tag (name, sortOrder) VALUES (?, ?)",
                        arrayOf(name, i)
                    )
                }
            }
        }
    }
}
