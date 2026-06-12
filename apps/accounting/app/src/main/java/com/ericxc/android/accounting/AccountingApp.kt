package com.ericxc.android.accounting

import android.app.Application

class AccountingApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AccountingApp
            private set
    }
}
