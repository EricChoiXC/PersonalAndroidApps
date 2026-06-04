package com.personal.accounting.feature.home

import android.content.Intent
import com.personal.accounting.base.BaseActivity
import com.personal.accounting.databinding.ActivityHomeBinding
import com.personal.accounting.feature.accounting.AccountingActivity
import com.personal.accounting.feature.statistics.StatisticsActivity
import com.personal.accounting.feature.config.ConfigActivity

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override fun onCreateBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mBinding.cardAccounting.setOnClickListener {
            startActivity(Intent(this, AccountingActivity::class.java))
        }
        mBinding.cardStatistics.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
        mBinding.cardConfig.setOnClickListener {
            startActivity(Intent(this, ConfigActivity::class.java))
        }
    }
}
