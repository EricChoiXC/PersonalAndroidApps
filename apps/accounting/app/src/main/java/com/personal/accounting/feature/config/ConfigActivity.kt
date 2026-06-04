package com.personal.accounting.feature.config

import com.personal.accounting.base.BaseActivity
import com.personal.accounting.databinding.ActivityConfigBinding

class ConfigActivity : BaseActivity<ActivityConfigBinding>() {

    override fun onCreateBinding(): ActivityConfigBinding {
        return ActivityConfigBinding.inflate(layoutInflater)
    }
}
