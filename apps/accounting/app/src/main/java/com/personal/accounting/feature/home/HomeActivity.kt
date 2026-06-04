package com.personal.accounting.feature.home

import android.os.Bundle
import com.personal.accounting.base.BaseActivity
import com.personal.accounting.databinding.ActivityHomeBinding

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override fun onCreateBinding(): ActivityHomeBinding {
        return ActivityHomeBinding.inflate(layoutInflater)
    }

    override fun initView() {
    }
}
