package com.ericxc.android.accounting.base

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var mBinding: VB
        private set

    private var mIsViewCreated = false
    private var mIsFirstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = onCreateBinding()
        setContentView(mBinding.root)
        initStatusBar()
        initView()
        mIsViewCreated = true
        if (mIsFirstLoad) {
            onLazyLoad()
            mIsFirstLoad = false
        }
    }

    protected abstract fun onCreateBinding(): VB

    protected open fun initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        window.statusBarColor = getStatusBarColor()
    }

    protected open fun getStatusBarColor(): Int {
        return androidx.core.content.ContextCompat.getColor(this, com.ericxc.android.accounting.R.color.brand_primary)
    }

    protected open fun initView() {}

    protected open fun onLazyLoad() {}

    override fun onDestroy() {
        super.onDestroy()
        mIsViewCreated = false
    }
}
