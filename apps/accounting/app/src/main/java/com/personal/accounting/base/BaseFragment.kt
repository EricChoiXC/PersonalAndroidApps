package com.personal.accounting.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val mBinding: VB get() = _binding!!

    private var mIsViewCreated = false
    private var mIsFirstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = onCreateBinding(inflater, container)
        return mBinding.root
    }

    protected abstract fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): VB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIsViewCreated = true
        initView()
        if (mIsFirstLoad) {
            onLazyLoad()
            mIsFirstLoad = false
        }
    }

    protected open fun initView() {}

    protected open fun onLazyLoad() {}

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && mIsViewCreated && mIsFirstLoad) {
            onLazyLoad()
            mIsFirstLoad = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mIsViewCreated = false
    }
}
