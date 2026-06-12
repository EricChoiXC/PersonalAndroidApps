package com.ericxc.android.accounting.feature.home

import android.content.Intent
import com.ericxc.android.accounting.base.BaseActivity
import com.ericxc.android.accounting.databinding.ActivityHomeBinding
import com.ericxc.android.accounting.feature.accounting.AccountingActivity
import com.ericxc.android.accounting.feature.statistics.StatisticsActivity
import com.ericxc.android.accounting.feature.config.ConfigActivity

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
