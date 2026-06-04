package com.personal.accounting.feature.accounting

import com.personal.accounting.base.BaseActivity
import com.personal.accounting.databinding.ActivityAccountingBinding

class AccountingActivity : BaseActivity<ActivityAccountingBinding>() {

    override fun onCreateBinding(): ActivityAccountingBinding {
        return ActivityAccountingBinding.inflate(layoutInflater)
    }
}
