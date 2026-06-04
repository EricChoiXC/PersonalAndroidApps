package com.personal.accounting.feature.statistics

import android.app.DatePickerDialog
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.personal.accounting.R
import com.personal.accounting.base.BaseActivity
import com.personal.accounting.data.AccountRepository
import com.personal.accounting.data.BillRepository
import com.personal.accounting.data.TagRepository
import com.personal.accounting.data.local.AppDatabase
import com.personal.accounting.data.model.AccountEntity
import com.personal.accounting.data.model.BillEntity
import com.personal.accounting.data.model.TagEntity
import com.personal.accounting.databinding.ActivityStatisticsBinding
import com.personal.accounting.util.DateUtils
import com.personal.accounting.util.ExportUtil
import kotlinx.coroutines.launch
import java.util.Calendar

class StatisticsActivity : BaseActivity<ActivityStatisticsBinding>() {

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
    private var mTagList = listOf<TagEntity>()
    private var mBillList = listOf<BillEntity>()
    private lateinit var mAdapter: BillAdapter

    private var mStartDate: Long? = null
    private var mEndDate: Long? = null

    override fun onCreateBinding(): ActivityStatisticsBinding {
        return ActivityStatisticsBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mBinding.rvResults.layoutManager = LinearLayoutManager(this)
        loadAccounts()
        loadTags()
        setupTypeSpinner()
        setupDateButtons()
        setupQuickDateButtons()
        mBinding.btnQuery.setOnClickListener { performQuery() }
        mBinding.btnExport.setOnClickListener { performExport() }
    }

    override fun onLazyLoad() {
        performQuery()
    }

    private fun loadAccounts() {
        lifecycleScope.launch {
            mAccountList = mAccountRepository.getAll()
            val names = mutableListOf(getString(R.string.statistics_all))
            names.addAll(mAccountList.map { it.name })
            val adapter = ArrayAdapter(
                this@StatisticsActivity,
                android.R.layout.simple_spinner_item,
                names
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mBinding.spnAccount.adapter = adapter
        }
    }

    private fun loadTags() {
        lifecycleScope.launch {
            mTagList = mTagRepository.getAll()
            mBinding.llTagContainer.removeAllViews()
            for (tag in mTagList) {
                val cb = CheckBox(this@StatisticsActivity).apply {
                    text = tag.name
                    setTag(tag.id)
                    setTextColor(resources.getColor(R.color.text_primary, theme))
                    textSize = 14f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { topMargin = 4 }
                }
                mBinding.llTagContainer.addView(cb)
            }
        }
    }

    private fun setupTypeSpinner() {
        val types = listOf(
            getString(R.string.statistics_all),
            getString(R.string.accounting_expense),
            getString(R.string.accounting_income)
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mBinding.spnType.adapter = adapter
    }

    private fun setupDateButtons() {
        mBinding.btnStartDate.setOnClickListener {
            showDatePickerDialog { year, month, day ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, day, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                mStartDate = cal.timeInMillis
                mBinding.btnStartDate.text = DateUtils.formatDisplay(mStartDate!!)
                validateDateRange()
            }
        }
        mBinding.btnEndDate.setOnClickListener {
            showDatePickerDialog { year, month, day ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, day, 23, 59, 59)
                    set(Calendar.MILLISECOND, 999)
                }
                mEndDate = cal.timeInMillis
                mBinding.btnEndDate.text = DateUtils.formatDisplay(mEndDate!!)
                validateDateRange()
            }
        }
    }

    private fun setupQuickDateButtons() {
        mBinding.btnSelectYear.setOnClickListener {
            val currentYear = DateUtils.getCurrentYear()
            showYearPickerDialog(currentYear) { year ->
                val range = DateUtils.getYearRange(year)
                mStartDate = range.first
                mEndDate = range.second
                mBinding.btnStartDate.text = DateUtils.formatDisplay(mStartDate!!)
                mBinding.btnEndDate.text = DateUtils.formatDisplay(mEndDate!!)
                performQuery()
            }
        }
        mBinding.btnSelectMonth.setOnClickListener {
            val currentYear = DateUtils.getCurrentYear()
            val currentMonth = DateUtils.getCurrentMonth()
            showMonthPickerDialog(currentYear, currentMonth) { year, month ->
                val range = DateUtils.getMonthRange(year, month)
                mStartDate = range.first
                mEndDate = range.second
                mBinding.btnStartDate.text = DateUtils.formatDisplay(mStartDate!!)
                mBinding.btnEndDate.text = DateUtils.formatDisplay(mEndDate!!)
                performQuery()
            }
        }
    }

    private fun showDatePickerDialog(onDateSet: (Int, Int, Int) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day -> onDateSet(year, month, day) },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showYearPickerDialog(currentYear: Int, onYearSet: (Int) -> Unit) {
        val years = (currentYear downTo currentYear - 10).toList()
        val items = years.map { it.toString() }.toTypedArray()
        android.app.AlertDialog.Builder(this)
            .setTitle(R.string.statistics_select_year)
            .setItems(items) { _, which ->
                onYearSet(years[which])
            }
            .show()
    }

    private fun showMonthPickerDialog(
        currentYear: Int,
        currentMonth: Int,
        onMonthSet: (Int, Int) -> Unit
    ) {
        val years = (currentYear downTo currentYear - 10).toList()
        val yearItems = years.map { "${it}年" }.toTypedArray()
        android.app.AlertDialog.Builder(this)
            .setTitle(R.string.statistics_select_year)
            .setItems(yearItems) { _, yearIndex ->
                val year = years[yearIndex]
                val months = (1..12).map { "${it}月" }.toTypedArray()
                android.app.AlertDialog.Builder(this)
                    .setTitle("${year}年")
                    .setItems(months) { _, monthIndex ->
                        onMonthSet(year, monthIndex + 1)
                    }
                    .show()
            }
            .show()
    }

    private fun validateDateRange(): Boolean {
        if (mStartDate != null && mEndDate != null) {
            if (!DateUtils.isWithinOneYear(mStartDate!!, mEndDate!!)) {
                Toast.makeText(
                    this,
                    R.string.statistics_date_range_exceed,
                    Toast.LENGTH_SHORT
                ).show()
                mEndDate = null
                mBinding.btnEndDate.text = getString(R.string.statistics_end_date)
                return false
            }
        }
        return true
    }

    private fun performQuery() {
        lifecycleScope.launch {
            val type = when (mBinding.spnType.selectedItemPosition) {
                1 -> 0
                2 -> 1
                else -> null
            }

            val accountId = if (mBinding.spnAccount.selectedItemPosition > 0) {
                mAccountList.getOrNull(mBinding.spnAccount.selectedItemPosition - 1)?.id
            } else null

            val amountMin = mBinding.etAmountMin.text.toString().trim().toDoubleOrNull()
            val amountMax = mBinding.etAmountMax.text.toString().trim().toDoubleOrNull()

            if ((amountMin != null && amountMax != null) && amountMin > amountMax) {
                Toast.makeText(
                    this@StatisticsActivity,
                    R.string.statistics_invalid_amount,
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }

            val selectedTagIds = mutableListOf<Long>()
            for (i in 0 until mBinding.llTagContainer.childCount) {
                val child = mBinding.llTagContainer.getChildAt(i)
                if (child is CheckBox && child.isChecked) {
                    (child.tag as? Long)?.let { selectedTagIds.add(it) }
                }
            }

            mBillList = mBillRepository.query(
                type = type,
                accountId = accountId,
                amountMin = amountMin,
                amountMax = amountMax,
                tagIds = selectedTagIds.ifEmpty { null },
                startDate = mStartDate,
                endDate = mEndDate
            )

            updateSummary()
            updateResultList()
        }
    }

    private fun updateSummary() {
        val totalIncome = mBillList.filter { it.type == 1 }.sumOf { it.amount }
        val totalExpense = mBillList.filter { it.type == 0 }.sumOf { it.amount }
        val netAmount = totalIncome - totalExpense

        mBinding.tvTotalIncome.text = String.format("%.2f", totalIncome)
        mBinding.tvTotalExpense.text = String.format("%.2f", totalExpense)
        mBinding.tvNetAmount.text = String.format("%.2f", netAmount)

        mBinding.tvNetAmount.setTextColor(
            if (netAmount >= 0) getColor(R.color.income) else getColor(R.color.expense)
        )
    }

    private fun updateResultList() {
        if (mBillList.isEmpty()) {
            mBinding.tvResultCount.visibility = View.VISIBLE
            mBinding.tvResultCount.text = getString(R.string.hint_no_data)
        } else {
            mBinding.tvResultCount.visibility = View.VISIBLE
            mBinding.tvResultCount.text =
                getString(R.string.statistics_result_count, mBillList.size)
        }
        if (!::mAdapter.isInitialized) {
            val accountMap = mAccountList.associateBy { it.id }
            val tagMap = mTagList.associateBy { it.id }
            mAdapter = BillAdapter(accountMap, tagMap)
            mBinding.rvResults.adapter = mAdapter
        }
        mAdapter.submitList(mBillList)
    }

    private fun performExport() {
        lifecycleScope.launch {
            try {
                val accounts = mAccountRepository.getAll()
                val tags = mTagRepository.getAll()
                val path = ExportUtil.export(
                    this@StatisticsActivity,
                    mBillList,
                    accounts,
                    tags
                )
                if (path != null) {
                    Toast.makeText(
                        this@StatisticsActivity,
                        "${getString(R.string.toast_export_success)} $path",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this@StatisticsActivity,
                        R.string.toast_export_failed,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@StatisticsActivity,
                    R.string.toast_export_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
