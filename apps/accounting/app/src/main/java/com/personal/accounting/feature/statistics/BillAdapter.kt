package com.personal.accounting.feature.statistics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.personal.accounting.R
import com.personal.accounting.data.model.AccountEntity
import com.personal.accounting.data.model.BillEntity
import com.personal.accounting.data.model.TagEntity
import com.personal.accounting.databinding.ItemBillBinding
import com.personal.accounting.util.DateUtils

class BillAdapter(
    private val mAccountMap: Map<Long, AccountEntity>,
    private val mTagMap: Map<Long, TagEntity>
) : RecyclerView.Adapter<BillAdapter.ViewHolder>() {

    private var mList = listOf<BillEntity>()

    fun submitList(list: List<BillEntity>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBillBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int = mList.size

    inner class ViewHolder(private val mBinding: ItemBillBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(bill: BillEntity) {
            val context = mBinding.root.context
            if (bill.type == 0) {
                mBinding.tvType.text = context.getString(R.string.item_bill_type_expense)
                mBinding.tvType.setTextColor(context.getColor(R.color.expense))
                mBinding.tvAmount.setTextColor(context.getColor(R.color.expense))
            } else {
                mBinding.tvType.text = context.getString(R.string.item_bill_type_income)
                mBinding.tvType.setTextColor(context.getColor(R.color.income))
                mBinding.tvAmount.setTextColor(context.getColor(R.color.income))
            }
            mBinding.tvAccount.text = mAccountMap[bill.accountId]?.name ?: ""
            mBinding.tvAmount.text = String.format("%.2f", bill.amount)
            val tagNames = bill.tagIds.split(",").mapNotNull {
                it.toLongOrNull()?.let { id -> mTagMap[id]?.name }
            }.joinToString("、")
            mBinding.tvTags.text = tagNames
            mBinding.tvDate.text = DateUtils.formatDisplay(bill.billDate)
        }
    }
}
