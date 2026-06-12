package com.ericxc.android.accounting.feature.config

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import com.ericxc.android.accounting.R
import com.ericxc.android.accounting.base.BaseActivity
import com.ericxc.android.accounting.data.AccountRepository
import com.ericxc.android.accounting.data.TagRepository
import com.ericxc.android.accounting.data.local.AppDatabase
import com.ericxc.android.accounting.data.model.AccountEntity
import com.ericxc.android.accounting.data.model.TagEntity
import com.ericxc.android.accounting.databinding.ActivityConfigBinding
import com.ericxc.android.accounting.databinding.ItemConfigEntryBinding
import kotlinx.coroutines.launch

class ConfigActivity : BaseActivity<ActivityConfigBinding>() {

    private val mAccountRepository: AccountRepository by lazy {
        AccountRepository(AppDatabase.getInstance(this).accountDao())
    }
    private val mTagRepository: TagRepository by lazy {
        TagRepository(AppDatabase.getInstance(this).tagDao())
    }

    override fun onCreateBinding(): ActivityConfigBinding {
        return ActivityConfigBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mBinding.btnAddAccount.setOnClickListener { showAddAccountDialog() }
        mBinding.btnAddTag.setOnClickListener { showAddTagDialog() }
    }

    override fun onLazyLoad() {
        loadAccounts()
        loadTags()
    }

    private fun loadAccounts() {
        lifecycleScope.launch {
            val accounts = mAccountRepository.getAll()
            mBinding.llAccountContainer.removeAllViews()
            for (account in accounts) {
                mBinding.llAccountContainer.addView(createAccountItemView(account))
            }
            if (accounts.isEmpty()) {
                mBinding.llAccountContainer.addView(createEmptyView(R.string.hint_no_data))
            }
        }
    }

    private fun loadTags() {
        lifecycleScope.launch {
            val tags = mTagRepository.getAll()
            mBinding.llTagContainer.removeAllViews()
            for (tag in tags) {
                mBinding.llTagContainer.addView(createTagItemView(tag))
            }
            if (tags.isEmpty()) {
                mBinding.llTagContainer.addView(createEmptyView(R.string.hint_no_data))
            }
        }
    }

    private fun createAccountItemView(account: AccountEntity): View {
        val binding = ItemConfigEntryBinding.inflate(LayoutInflater.from(this))
        binding.tvName.text = account.name
        binding.tvSubtitle.text = account.accountNumber.ifEmpty { getString(R.string.config_account_number_hint) }
        binding.tvSubtitle.visibility = View.VISIBLE
        binding.btnEdit.setOnClickListener { showEditAccountDialog(account) }
        binding.btnDelete.setOnClickListener { showDeleteAccountDialog(account) }
        return binding.root
    }

    private fun createTagItemView(tag: TagEntity): View {
        val binding = ItemConfigEntryBinding.inflate(LayoutInflater.from(this))
        binding.tvName.text = tag.name
        binding.tvSubtitle.visibility = View.GONE
        binding.btnEdit.setOnClickListener { showEditTagDialog(tag) }
        binding.btnDelete.setOnClickListener { showDeleteTagDialog(tag) }
        return binding.root
    }

    private fun createEmptyView(textRes: Int): View {
        val tv = TextView(this)
        tv.setText(textRes)
        tv.setTextColor(resources.getColor(R.color.text_secondary, theme))
        tv.textSize = 14f
        tv.setPadding(0, 16, 0, 16)
        tv.gravity = android.view.Gravity.CENTER
        return tv
    }

    private fun showAddAccountDialog() {
        val inputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }
        val nameInput = EditText(this).apply {
            hint = getString(R.string.hint_name)
            setTextColor(resources.getColor(R.color.text_primary, theme))
            setHintTextColor(resources.getColor(R.color.text_secondary, theme))
            textSize = 16f
        }
        val numberInput = EditText(this).apply {
            hint = getString(R.string.config_account_number_hint)
            setTextColor(resources.getColor(R.color.text_primary, theme))
            setHintTextColor(resources.getColor(R.color.text_secondary, theme))
            textSize = 16f
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        inputLayout.addView(nameInput)
        inputLayout.addView(numberInput)

        AlertDialog.Builder(this)
            .setTitle(R.string.config_add_account)
            .setView(inputLayout)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = nameInput.text.toString().trim()
                if (name.isNotEmpty()) {
                    lifecycleScope.launch {
                        mAccountRepository.insert(
                            AccountEntity(name = name, accountNumber = numberInput.text.toString().trim())
                        )
                        loadAccounts()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showEditAccountDialog(account: AccountEntity) {
        val inputLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }
        val nameInput = EditText(this).apply {
            setText(account.name)
            setSelection(account.name.length)
            setTextColor(resources.getColor(R.color.text_primary, theme))
            setHintTextColor(resources.getColor(R.color.text_secondary, theme))
            textSize = 16f
        }
        val numberInput = EditText(this).apply {
            setText(account.accountNumber)
            setSelection(account.accountNumber.length)
            setTextColor(resources.getColor(R.color.text_primary, theme))
            setHintTextColor(resources.getColor(R.color.text_secondary, theme))
            textSize = 16f
            inputType = InputType.TYPE_CLASS_NUMBER
        }
        inputLayout.addView(nameInput)
        inputLayout.addView(numberInput)

        AlertDialog.Builder(this)
            .setTitle(R.string.config_edit_account)
            .setView(inputLayout)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = nameInput.text.toString().trim()
                if (name.isNotEmpty()) {
                    lifecycleScope.launch {
                        mAccountRepository.update(
                            account.copy(name = name, accountNumber = numberInput.text.toString().trim())
                        )
                        loadAccounts()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteAccountDialog(account: AccountEntity) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_confirm_delete)
            .setMessage(getString(R.string.dialog_confirm_delete_account, account.name))
            .setPositiveButton(R.string.confirm) { _, _ ->
                lifecycleScope.launch {
                    mAccountRepository.delete(account)
                    loadAccounts()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showAddTagDialog() {
        val inputLayout = LinearLayout(this).apply {
            setPadding(48, 24, 48, 0)
        }
        val nameInput = EditText(this).apply {
            hint = getString(R.string.hint_name)
            setTextColor(resources.getColor(R.color.text_primary, theme))
            setHintTextColor(resources.getColor(R.color.text_secondary, theme))
            textSize = 16f
        }
        inputLayout.addView(nameInput)

        AlertDialog.Builder(this)
            .setTitle(R.string.config_add_tag)
            .setView(inputLayout)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = nameInput.text.toString().trim()
                if (name.isNotEmpty()) {
                    lifecycleScope.launch {
                        mTagRepository.insert(TagEntity(name = name))
                        loadTags()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showEditTagDialog(tag: TagEntity) {
        val inputLayout = LinearLayout(this).apply {
            setPadding(48, 24, 48, 0)
        }
        val nameInput = EditText(this).apply {
            setText(tag.name)
            setSelection(tag.name.length)
            setTextColor(resources.getColor(R.color.text_primary, theme))
            setHintTextColor(resources.getColor(R.color.text_secondary, theme))
            textSize = 16f
        }
        inputLayout.addView(nameInput)

        AlertDialog.Builder(this)
            .setTitle(R.string.config_edit_tag)
            .setView(inputLayout)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = nameInput.text.toString().trim()
                if (name.isNotEmpty()) {
                    lifecycleScope.launch {
                        mTagRepository.update(tag.copy(name = name))
                        loadTags()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showDeleteTagDialog(tag: TagEntity) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_confirm_delete)
            .setMessage(getString(R.string.dialog_confirm_delete_tag, tag.name))
            .setPositiveButton(R.string.confirm) { _, _ ->
                lifecycleScope.launch {
                    mTagRepository.delete(tag)
                    loadTags()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
