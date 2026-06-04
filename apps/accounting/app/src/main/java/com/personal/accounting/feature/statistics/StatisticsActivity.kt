package com.personal.accounting.feature.statistics

import com.personal.accounting.base.BaseActivity
import com.personal.accounting.databinding.ActivityStatisticsBinding

class StatisticsActivity : BaseActivity<ActivityStatisticsBinding>() {

    override fun onCreateBinding(): ActivityStatisticsBinding {
        return ActivityStatisticsBinding.inflate(layoutInflater)
    }
}
