package com.community.mingle.views.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.community.mingle.R
import com.community.mingle.databinding.ItemMypageBinding
import com.community.mingle.service.models.Option
import com.community.mingle.BR

class OptionListAdapter(private val optionList: ArrayList<Option>) :
    RecyclerView.Adapter<OptionListAdapter.OptionViewHolder>() {

    override fun getItemCount(): Int = optionList.size

    interface OnOptionClickListener {
        fun onClickOption(item: Option, position: Int)
    }

    private lateinit var optionClickListener: OnOptionClickListener

    fun setOnOptionClickListener(listener: OnOptionClickListener) {
        this.optionClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OptionListAdapter.OptionViewHolder {
        val binding: ItemMypageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_mypage,
            parent,
            false
        )

        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionListAdapter.OptionViewHolder, position: Int) {
        holder.bind(optionList[position], position)
    }

    inner class OptionViewHolder(
        private val binding: ItemMypageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Option, position: Int) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
            binding.root.setOnClickListener {
                optionClickListener.onClickOption(item, position)
            }
        }
    }
}