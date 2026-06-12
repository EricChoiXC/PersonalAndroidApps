package com.ericxc.android.accounting.feature.accounting

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.ericxc.android.accounting.R
import com.ericxc.android.accounting.base.BaseActivity
import com.ericxc.android.accounting.data.AccountRepository
import com.ericxc.android.accounting.data.BillRepository
import com.ericxc.android.accounting.data.TagRepository
import com.ericxc.android.accounting.data.local.AppDatabase
import com.ericxc.android.accounting.data.model.AccountEntity
import com.ericxc.android.accounting.data.model.BillEntity
import com.ericxc.android.accounting.databinding.ActivityAccountingBinding
import com.ericxc.android.accounting.util.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class AccountingActivity : BaseActivity<ActivityAccountingBinding>() {

    private val mAccountRepository: AccountRepository by lazy {
        AccountRepository(AppDatabase.getInstance(this).accountDao())
    }
    private val mTagRepository: TagRepository by lazy {
        TagRepository(AppDatabase.getInstance(this).tagDao())
    }
    private val mBillRepository: BillRepository by lazy {
        BillRepository(AppDatabase.getInstance(this).billDao())
    }

    private var mAccountList = listOf<AccountEntity>()
    private var mBillDate: Long = System.currentTimeMillis()

    override fun onCreateBinding(): ActivityAccountingBinding {
        return ActivityAccountingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        loadAccounts()
        loadTags()
        setupAccountSpinnerListener()
        setupBillDatePicker()
        mBinding.btnSubmit.setOnClickListener { submitBill() }
    }

    private fun loadAccounts() {
        lifecycleScope.launch {
            mAccountList = mAccountRepository.getAll()
            val names = mAccountList.map { it.name }
            val adapter = ArrayAdapter(
                this@AccountingActivity,
                android.R.layout.simple_spinner_item,
                names
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mBinding.spnAccount.adapter = adapter
        }
    }

    private fun loadTags() {
        lifecycleScope.launch {
            val tagList = mTagRepository.getAll()
            mBinding.llTagContainer.removeAllViews()
            for (tag in tagList) {
                val cb = CheckBox(this@AccountingActivity).apply {
                    text = tag.name
                    setTag(tag.id)
                    setTextColor(resources.getColor(R.color.text_primary, theme))
                    textSize = 16f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = 8 }
                }
                mBinding.llTagContainer.addView(cb)
            }
        }
    }

    private fun setupAccountSpinnerListener() {
        mBinding.spnAccount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position >= 0 && position < mAccountList.size) {
                    mBinding.etAccountNumber.setText(mAccountList[position].accountNumber)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupBillDatePicker() {
        updateBillDateDisplay()
        mBinding.tvBillDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = mBillDate }
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    TimePickerDialog(
                        this,
                        { _, hour, minute ->
                            val selected = Calendar.getInstance().apply {
                                set(year, month, day, hour, minute, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            mBillDate = selected.timeInMillis
                            updateBillDateDisplay()
                        },
                        cal.get(Calendar.HOUR_OF_DAY),
                        cal.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateBillDateDisplay() {
        mBinding.tvBillDate.text = DateUtils.formatDateTimeDisplay(mBillDate)
    }

    private fun submitBill() {
        val amountStr = mBinding.etAmount.text.toString().trim()
        if (amountStr.isEmpty()) {
            mBinding.etAmount.error = getString(R.string.toast_required)
            mBinding.etAmount.requestFocus()
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null) {
            mBinding.etAmount.error = getString(R.string.toast_required)
            mBinding.etAmount.requestFocus()
            return
        }

        if (mAccountList.isEmpty()) {
            Toast.makeText(this, R.string.toast_required, Toast.LENGTH_SHORT).show()
            return
        }

        val account = mAccountList[mBinding.spnAccount.selectedItemPosition]
        val accountNumber = mBinding.etAccountNumber.text.toString().trim()
        val remark = mBinding.etRemark.text.toString().trim()

        val selectedTagIds = mutableListOf<Long>()
        for (i in 0 until mBinding.llTagContainer.childCount) {
            val child = mBinding.llTagContainer.getChildAt(i)
            if (child is CheckBox && child.isChecked) {
                (child.tag as? Long)?.let { selectedTagIds.add(it) }
            }
        }

        val type = if (mBinding.rbIncome.isChecked) 1 else 0

        val bill = BillEntity(
            type = type,
            accountId = account.id,
            accountNumber = accountNumber,
            amount = amount,
            tagIds = selectedTagIds.joinToString(","),
            remark = remark,
            billDate = mBillDate
        )

        lifecycleScope.launch {
            try {
                mBillRepository.insert(bill)
                Toast.makeText(
                    this@AccountingActivity,
                    R.string.toast_save_success,
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this@AccountingActivity,
                    R.string.toast_save_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
